package com.pundix.core.order;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.binance.dex.api.client.domain.jsonrpc.ABCIQueryResult;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pundix.common.http.OkHttpClientConfig;
import com.pundix.common.utils.GsonUtils;
import com.pundix.common.utils.StringUtils;
import com.pundix.core.FunctionxNodeConfig;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.model.Gas;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.hub.FxBalanceModel;
import com.pundix.core.model.NodeModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderTransation implements ITransation {
    private static final String TAG = "OrderTransation";

    public OrderTransation() {
    }

    @Override
    public TransationResult sendTransation(TransationData transationData) throws Exception {
        OrderTransationData hubTransationData = (OrderTransationData) transationData;
        AccKey accKey = AccKey.fromPriKey(hubTransationData.getPrivateKey());
         if(TextUtils.isEmpty(hubTransationData.getGasPrice())){
             Gas fee = (Gas) getFee(transationData);
             hubTransationData.setGasLimit(fee.getGasLimit());
             hubTransationData.setGasPrice(fee.getGasPrice());
         }
        String fee= new BigDecimal(hubTransationData.getGasPrice()).multiply(new BigDecimal(hubTransationData.getGasLimit())).stripTrailingZeros().toPlainString();
        Transaction tx = Transaction.builder()
                .memo("")
                .feeGas(hubTransationData.getGasLimit())
                .addFeeAmount(Coin.PAYC.getSymbol().toLowerCase(), fee)
                .addMsg(
                        MsgSendValue.builder()
                                .addAmount(hubTransationData.getAmount().getDenom(), hubTransationData.getAmount().getAmount())
                                .fromAddress(hubTransationData.getFromAddress())
                                .toAddress(hubTransationData.getToAddress())
                )
                .build();

        final RpcResp<BroadcastTxSyncResult> resultRpcResp = buildClient().broadcast(tx, RpcMethod.Broadcast.TX_COMMIT, accKey);
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
    public List<String> getArrayBalance(String... address) {
        List<RpcResp<ABCIQueryResult>> rpcResp = buildClient().account(address);
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
                e.printStackTrace();
                amountArray.add(null);
            }
        }
        return amountArray;
    }
    @Override
    public Object getFee(TransationData data) throws Exception {
        OrderTransationData hubTransationData = (OrderTransationData) data;
        Gas gas = new Gas();
        final Client client = buildClient();
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

    private Client buildClient() {
        NodeModel nodeConfig = FunctionxNodeConfig.getInstance().getNodeConfig(Coin.PAYC);
        RpcOptions rpcOptionsSms = RpcOptions.builder()
                .chainId(nodeConfig.getChainId())
                .nodeUrl(nodeConfig.getUrl())
                .hrp(Coin.PAYC.getHrp())
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
