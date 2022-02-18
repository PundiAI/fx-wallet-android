package com.pundix.core.fxcore;

import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.Any1;
import com.googlecode.protobuf.format.JsonFormat;
import com.pundix.common.utils.GsonUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.enums.MsgType;
import com.pundix.core.ethereum.model.Gas;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.model.FxData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cosmos.bank.v1beta1.Tx;
import cosmos.base.abci.v1beta1.Abci;
import cosmos.base.v1beta1.CoinOuterClass;
import cosmos.tx.v1beta1.ServiceOuterClass;
import cosmos.tx.v1beta1.TxOuterClass;
import fx.gravity.v1.Msgs;
import io.functionx.Client;
import io.functionx.acc.AccKey;
import io.functionx.config.Config;


/**
 * Description：FxCoreTransation
 * @author Carl
 * @date 2021/5/26
 */
public class FxCoreTransation implements ITransation {

    public static Map<String, Client> clientMap;

    @Override
    public TransationResult sendTransation(TransationData transationData) throws Exception {
        FunctionXTransationData functionXData = (FunctionXTransationData) transationData;
        AccKey accKey = AccKey.fromPriKey(functionXData.getPrivateKey());
        String feeSymobl ;
        if(functionXData.getCoin() == Coin.FX_PAYMENT){
            feeSymobl = functionXData.getCoin().getSymbol().toLowerCase();
        }else {
            feeSymobl = functionXData.getCoin().getSymbol();
        }
        final Client functionxClient = buildClient(functionXData);
        final TxOuterClass.TxBody.Builder transationBuilder = getTransationBuilder(functionXData);
        String fee = new BigInteger(functionXData.getGasLimit()).multiply(new BigInteger(functionXData.getGasPrice())).toString();
        final TxOuterClass.Fee feeBuild = TxOuterClass.Fee.newBuilder()
                .setGasLimit(Long.parseLong(functionXData.getGasLimit()))
                .addAmount(CoinOuterClass.Coin.newBuilder()
                        .setAmount(fee)
                        .setDenom(feeSymobl).build()).build();
        Abci.TxResponse broadcast = functionxClient.broadcast(transationBuilder, accKey, feeBuild, ServiceOuterClass.BroadcastMode.BROADCAST_MODE_SYNC);
        TransationResult transationResult = new TransationResult();
        if (broadcast.getCode() == 0) {
            transationResult.setCode(0);
            transationResult.setHash(broadcast.getTxhash());
        } else {
            transationResult.setCode(-1);
            transationResult.setMsg(broadcast.getRawLog());
        }
        return transationResult;
    }


    @Override
    public String getBalance(String... address) {
        return null;
    }

    @Override
    public List<String> getArrayBalance(String... address) {
        return null;
    }

    public String getFxBalance(FunctionXTransationData noneTransationData, String... address) {
        final Client client = buildClient(noneTransationData);
        String denom = noneTransationData.getAmount().getDenom();
        if (denom.toLowerCase().equals("payc")) {
            denom = denom.toLowerCase();
        }
        final CoinOuterClass.Coin balance = client.getQuerier().balance(address[0], denom);

        return balance.getAmount();
    }


    public List<String> getFxArrayBalance(FunctionXTransationData noneTransationData, String... address) {
        final Client client = buildClient(noneTransationData);
        List<String> array = new ArrayList<>();
        for (String add : address) {
            try {
                String denom = noneTransationData.getAmount().getDenom();
                if (denom.toLowerCase().equals("payc")) {
                    denom = denom.toLowerCase();
                }
                String amount = client.getQuerier().balance(add, denom).getAmount();
                array.add(amount);
            } catch (Exception e) {
                e.printStackTrace();
                array.add(null);
            }
        }

        return array;
    }

    @Override
    public Object getFee(TransationData data) throws Exception {
        FunctionXTransationData hubTransationData = (FunctionXTransationData) data;
        Gas gas = new Gas();
        final Client client = buildClient(hubTransationData);
        final AccKey accKey = AccKey.fromPriKey(hubTransationData.getPrivateKey());
        client.prepareAccountInfo(accKey, ServiceOuterClass.BroadcastMode.BROADCAST_MODE_ASYNC);
        final TxOuterClass.AuthInfo.Builder authInfoBuilder = client.getAuthInfoBuilder(accKey);
        final TxOuterClass.TxBody.Builder transationBuilder = getTransationBuilder(hubTransationData);
        final TxOuterClass.Fee fee = client.getFee(transationBuilder, authInfoBuilder,accKey);
        final CoinOuterClass.Coin amount = fee.getAmount(0);
        gas.setGasPrice(new BigDecimal(amount.getAmount()).divide(new BigDecimal(fee.getGasLimit()), RoundingMode.DOWN).stripTrailingZeros().toPlainString());
        gas.setGasLimit("" + fee.getGasLimit());
        return gas;
    }

    @Override
    public Object getTxs(Object data) {
        try{
            FunctionXTransationData hubTransationData = (FunctionXTransationData) data;
            Client client = buildClient(hubTransationData);
            ServiceOuterClass.GetTxResponse txResponse = client.queryTx(hubTransationData.getHash());
            return txResponse;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    private final TxOuterClass.TxBody.Builder getTransationBuilder(FunctionXTransationData noneTransationData) {
        TxOuterClass.TxBody.Builder transactionBuilder = null;
        if (noneTransationData.getFxData() == null) {
            final Tx.MsgSend msgSend = Tx.MsgSend.newBuilder()
                    .setFromAddress(noneTransationData.getFromAddress())
                    .setToAddress(noneTransationData.getToAddress())
                    .addAmount(CoinOuterClass.Coin.newBuilder()
                            .setAmount(noneTransationData.getAmount().getAmount())
                            .setDenom(noneTransationData.getAmount().getDenom())
                            .build())
                    .build();

            transactionBuilder = TxOuterClass.TxBody.newBuilder()
                    .addMessages(Any1.pack(msgSend, ""));

        } else {
            final FxData fxData = noneTransationData.getFxData();
            final MsgType msgType = fxData.getMsgType();

            switch (msgType) {
                case MSG_DELEGATE: {
                    final cosmos.staking.v1beta1.Tx.MsgDelegate msgDelegate = cosmos.staking.v1beta1.Tx.MsgDelegate.newBuilder()
                            .setDelegatorAddress(fxData.getDelegatorAddress())
                            .setValidatorAddress(fxData.getValidatorAddress())
                            .setAmount(CoinOuterClass.Coin.newBuilder().setDenom(fxData.getAmount().getDenom()).setAmount(fxData.getAmount().getAmount())).build();
                    transactionBuilder = TxOuterClass.TxBody.newBuilder()
                            .addMessages(Any1.pack(msgDelegate, ""));
                }
                break;
                case MSG_UNDELEGATE: {
                    final cosmos.staking.v1beta1.Tx.MsgUndelegate msgUndelegate = cosmos.staking.v1beta1.Tx.MsgUndelegate.newBuilder()
                            .setDelegatorAddress((fxData.getDelegatorAddress()))
                            .setValidatorAddress((fxData.getValidatorAddress()))
                            .setAmount(CoinOuterClass.Coin.newBuilder().setDenom(fxData.getAmount().getDenom()).setAmount(fxData.getAmount().getAmount())).build();
                    transactionBuilder = TxOuterClass.TxBody.newBuilder()
                            .addMessages(Any1.pack(msgUndelegate, ""));
                }
                break;
                case MSG_WITHDRAW_DELEGATION_REWARD: {
                    cosmos.distribution.v1beta1.Tx.MsgWithdrawDelegatorReward msgWithdrawDelegatorReward = cosmos.distribution.v1beta1.Tx.MsgWithdrawDelegatorReward.newBuilder()
                            .setDelegatorAddress(fxData.getDelegatorAddress()).setValidatorAddress(fxData.getValidatorAddress()).build();
                    transactionBuilder = TxOuterClass.TxBody.newBuilder()
                            .addMessages(Any1.pack(msgWithdrawDelegatorReward, ""));
                }
                break;

                case MSG_SNED_TO_ETH:
                    Msgs.MsgSendToEth msgSendToEth = Msgs.MsgSendToEth.newBuilder()
                            .setBridgeFee(CoinOuterClass.Coin.newBuilder()
                                    .setAmount(fxData.getBridgeFee().getAmount())
                                    .setDenom(fxData.getBridgeFee().getDenom()))
                            .setAmount(CoinOuterClass.Coin.newBuilder().setAmount(fxData.getAmount().getAmount()).setDenom(fxData.getAmount().getDenom()).build())
                            .setSender(fxData.getFxSender())
                            .setEthDest(fxData.getEthereumReceiver()).build();
                    transactionBuilder = TxOuterClass.TxBody.newBuilder()
                            .addMessages(Any1.pack(msgSendToEth, ""));
                    break;
                case MSG_TRANSFER: {
                    long timeOutStamp = (System.currentTimeMillis() + 1000 * 60 * 60 * 24) * 1000000;
                    final ibc.fx.applications.transfer.v1.Tx.MsgTransfer.Builder transfer = ibc.fx.applications.transfer.v1.Tx.MsgTransfer.newBuilder()
                            .setSourcePort("transfer")
                            .setSourceChannel("channel-0")
                            .setToken(CoinOuterClass.Coin.newBuilder().setAmount(fxData.getAmount().getAmount()).setDenom(fxData.getAmount().getDenom()).build())
                            .setSender(noneTransationData.getFromAddress())
                            .setReceiver(noneTransationData.getToAddress())
                            .setTimeoutHeight(ibc.core.client.v1.Client.Height.newBuilder().setRevisionHeight(0).setRevisionNumber(0).build())
                            .setTimeoutTimestamp(timeOutStamp);
                    if(!TextUtils.isEmpty(fxData.getRouter())){
                        transfer.setRouter(fxData.getRouter());
                    }
                    transfer.setFee(CoinOuterClass.Coin.newBuilder().setAmount(fxData.getBridgeFee().getAmount()).setDenom(fxData.getBridgeFee().getDenom()).build());
                    transactionBuilder = TxOuterClass.TxBody.newBuilder()
                            .addMessages(Any1.pack(transfer.build(), ""));
                }
                break;
                default:
                    break;
            }
        }

        return transactionBuilder;
    }

    private Client buildClient(FunctionXTransationData noneTransationData) {
        if (clientMap == null) {
            clientMap = new HashMap<>();
        }
        Client client1 = clientMap.get(noneTransationData.getUrl() + noneTransationData.getHrp());
        if (client1 == null) {
            client1 = new Client(
                    new Config
                            .ConfigBuilder()
                            .withRpcUrl(noneTransationData.getUrl())
                            .withHrp(noneTransationData.getHrp())
                            .build());
            clientMap.put(noneTransationData.getUrl() + noneTransationData.getHrp(), client1);
        }

        return client1;
    }
}
