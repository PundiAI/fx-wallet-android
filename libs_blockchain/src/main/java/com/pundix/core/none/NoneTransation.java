package com.pundix.core.none;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.binance.dex.api.client.domain.jsonrpc.ABCIQueryResult;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pundix.core.bitcoin.model.Gas;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.hub.FxBalanceModel;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NoneTransation implements ITransation {
    private static final String TAG = "EthereumTransation";
    private NoneTransationData noneTransationData;

    public NoneTransation() {
    }

    @Override
    public TransationResult sendTransation(TransationData transationData) throws Exception {
        NoneTransationData none = (NoneTransationData) transationData;
        AccKey accKey = AccKey.fromPriKey(none.getPrivateKey());
        if (TextUtils.isEmpty(none.getGasPrice())) {
            Gas fee = (Gas) getFee(transationData);
            none.setGasLimit(fee.getGasLimit());
            none.setGasPrice(fee.getGasPrice());
        }
        String fee = new BigDecimal(none.getGasPrice()).multiply(new BigDecimal(none.getGasLimit())).stripTrailingZeros().toPlainString();
        Transaction tx = Transaction.builder()
                .memo("")
                .feeGas(none.getGasLimit())
                .addFeeAmount(none.getAmount().getDenom(), fee)
                .addMsg(
                        MsgSendValue.builder()
                                .addAmount(none.getAmount().getDenom(), none.getAmount().getAmount())
                                .fromAddress(none.getFromAddress())
                                .toAddress(none.getToAddress())
                )
                .build();

        final RpcResp<BroadcastTxSyncResult> resultRpcResp = buildClient(none).broadcast(tx, RpcMethod.Broadcast.TX_COMMIT, accKey);
        TransationResult transationResult = new TransationResult();
        if (resultRpcResp.getResult().getCheckTx().getCode() == 0 && resultRpcResp.getResult().getDeliverTx().getCode() == 0) {
            transationResult.setCode(0);
            transationResult.setHash(resultRpcResp.getResult().getHash());
        } else {
            if (resultRpcResp.getResult().getCheckTx().getCode() != 0) {
                transationResult.setMsg(StringUtils.logToMsg(resultRpcResp.getResult().getCheckTx().getLog()));
            } else if (resultRpcResp.getResult().getDeliverTx().getCode() != 0) {
                transationResult.setMsg(StringUtils.logToMsg(resultRpcResp.getResult().getDeliverTx().getLog()));
            }
            transationResult.setCode(-1);
        }
        return transationResult;

    }


    @Override
    public String getBalance(String... address) {

        return getArrayBalance(address).get(0);
    }

    @Override
    public List<String> getArrayBalance(String... address)  {
        List<RpcResp<ABCIQueryResult>> rpcResp = buildClient(noneTransationData).account(address);
        List<String> amountArray = new ArrayList<>();
        for (int i = 0; i < rpcResp.size(); i++) {
            try {
                final RpcResp<ABCIQueryResult> resultRpcResp = rpcResp.get(i);
                FxBalanceModel fxBalanceModel = GsonUtils.fromJson(resultRpcResp.getResult().getResponse().getValue(), FxBalanceModel.class);
                if (fxBalanceModel.getValue().getCoins().size() > 0) {
                    amountArray.add(fxBalanceModel.getValue().getCoins().get(0).getAmount());
                } else {
                    amountArray.add("0");
                }
            } catch (Exception e) {
                amountArray.add(null);
            }
        }
        return amountArray;
    }

    public void setNoneTransationData(NoneTransationData noneTransationData) {
        this.noneTransationData = noneTransationData;
    }

    @Override
    public Object getFee(TransationData data) throws Exception {
        NoneTransationData hubTransationData = (NoneTransationData) data;
        Gas gas = new Gas();
        final Client client = buildClient(hubTransationData);
        Amount otherGasPrice = getOtherGasPrice(client);
        gas.setGasPrice(otherGasPrice.getAmount());
        Transaction tx = Transaction.builder()
                .memo("")
                .addMsg(
                        MsgSendValue.builder()
                                .addAmount(hubTransationData.getAmount().getDenom(), hubTransationData.getAmount().getAmount())
                                .fromAddress(hubTransationData.getFromAddress())
                                .toAddress(hubTransationData.getToAddress())
                )
                .build();
        final String gasLimit = getOtherGasLimit(client, tx);
        gas.setGasLimit(gasLimit);
        return gas;
    }


    private Client buildClient(NoneTransationData noneTransationData) {
        RpcOptions rpcOptionsSms = RpcOptions.builder()
                .chainId(noneTransationData.getChainId())
                .nodeUrl(noneTransationData.getUrl())
                .hrp(noneTransationData.getHrp())
                .build();
        Client client = new Client(rpcOptionsSms, OkHttpClientConfig.getOkHttpClient());
        return client;
    }

    private Amount getOtherGasPrice(Client client) {
        RpcResp<ABCIQueryResult> resp = client.abciQuery(ABCIQueryParams.builder().path("custom/other/fees/gasPrice").build());
        String values = new String(Base64.decode(resp.getResult().getResponse().getValue(), Base64.DEFAULT));
        return subFeeAmount(values);

    }

    private static Amount subFeeAmount(String values) {
        Amount amounts = null;
        for (int i = values.length() - 1; i >= 0; i--) {
            if (Character.isDigit(values.charAt(i))) {
                String amount = values.substring(0, i + 1);
                String demon = values.substring(i + 1);
                amounts = Amount.builder().amount("" + new BigDecimal(amount).toBigInteger().toString()).denom(demon).build();
                return amounts;
            }
        }
        return amounts;
    }

    private String getOtherGasLimit(Client client, Transaction transaction) throws InvalidProtocolBufferException {
        final RpcResp<ABCIQueryResult> resultRpcResp = client.simulate(transaction);
        if (resultRpcResp.getResult().getResponse().getCode() == 0) {
            byte[] raw = AminoEncoding.aminoDecode(Base64.decode(resultRpcResp.getResult().getResponse().getValue(), Base64.DEFAULT), true, false);
            Base.ResponseCheckTx responseCheckTx = Base.ResponseCheckTx.parseFrom(raw);
            long gasUsed = responseCheckTx.getGasUsed() + 35000;
            Log.e("TAG", "getOtherEstimateGas: " + gasUsed);
            return String.valueOf(gasUsed);
        }
        throw new RuntimeException("ethEstimateGas error");
    }

}
