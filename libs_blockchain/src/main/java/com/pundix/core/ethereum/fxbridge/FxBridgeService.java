package com.pundix.core.ethereum.fxbridge;


import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.EthereumService;

import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.hutool.core.util.HexUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Description：FxBridgeService
 * @author joker
 * @date 2020/5/28
 */

public class FxBridgeService extends EthereumService {
    private static FxBridgeService fxBridgeService;
    public Web3j web3j;

    public static FxBridgeService getInstance() {
        if (fxBridgeService == null) {
            fxBridgeService = new FxBridgeService();
        }
        return fxBridgeService;

    }

    public FxBridgeService() {
        web3j = Web3j.build(new HttpService(Coin.ETHEREUM.getNodeUrl()));
    }


    public long fxNonce(String fromAddress, String fromFxAddress, String fxBridgeContractAddress) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        FxBridgeContract fxBridgeContract = FxBridgeContract.load(fxBridgeContractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        final BigInteger nonce = fxBridgeContract.fxNonce(fromFxAddress).send();
        return nonce.longValue();
    }


    public String oracle(String fromAddress, String fxBridgeContractAddress) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        FxBridgeContract fxBridgeContract = FxBridgeContract.load(fxBridgeContractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return fxBridgeContract.oracle().send();
    }


    public boolean validatorEnough(String fromAddress, String oracleAddress, String msgHash, String sign) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        FxBridgeContract fxBridgeContract = FxBridgeContract.load(oracleAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        final List<Type> typeList = fxBridgeContract.validatorEnough(msgHash, sign).send();
        Boolean bool = (Boolean) typeList.get(0).getValue();
        return bool;
    }

    public String getBridgeToken(String fromAddress, String hrp) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        FxBridgeContract bridgeManager = FxBridgeContract.load(FxBridgeContract.ManagerBridge, web3j, readonlyTransactionManager, new DefaultGasProvider());
        final List<Type> typeList = bridgeManager.bridgeRecords(hrp).send();
        String bridgeAddress = typeList.get(3).getValue().toString();
        if (bridgeAddress.equals("0x0000000000000000000000000000000000000000")) {
            throw new RuntimeException("bridge error");
        }
        return bridgeAddress;
    }


    public Disposable validBridgeToken(String fromAddress, String ercContractAddress, String hrp, Consumer<String> success, Consumer<Throwable> error) {
        return Observable.fromCallable(() -> {
            String bridgeAddress = getBridgeToken(fromAddress, hrp);
            if(TextUtils.isEmpty(ercContractAddress)){
                return bridgeAddress;
            }
            ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
            FxBridgeContract fxBridge = FxBridgeContract.load(bridgeAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
            Boolean isToken = (Boolean) fxBridge.validBridgeToken(ercContractAddress).send().get(0).getValue();
            if (!isToken) {
                throw new RuntimeException("erc20 error");
            }
            return bridgeAddress;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(success, error);

    }

    public JSONArray getBridgeTokenList(String fromAddress, String hrp) throws Exception {
        final String bridgeAddress = getBridgeToken(fromAddress, hrp);
        final String bridgeTokenListToAbi = FxBridgeContract.getBridgeTokenListToAbi();
        final String result = sendEthCall(bridgeAddress, bridgeAddress, bridgeTokenListToAbi).send().getResult();
        return decodeBridgeTokenList(result);
    }

    private JSONArray decodeBridgeTokenList(String resultData) {
        JSONArray jsonArray = new JSONArray();
        int PAD_LENGTH = 64;
        int ADDRESS_LENGTH = 40;
        int index = 0;
        resultData = Numeric.cleanHexPrefix(resultData);
        final BigInteger skipLength = Numeric.toBigInt(resultData.substring(index, index + PAD_LENGTH));
        index += PAD_LENGTH;
        final BigInteger tokenArrSize = Numeric.toBigInt(resultData.substring(index, index + PAD_LENGTH));
        index += PAD_LENGTH;
        BigInteger arrSkipLength;
        for (int i = 0; i < tokenArrSize.intValue(); i++) {
            arrSkipLength = Numeric.toBigInt(resultData.substring(index, index + ((i + 1) * PAD_LENGTH)));
            int structIndex = index + arrSkipLength.intValue() * 2;
            String address = (resultData.substring(structIndex, structIndex + PAD_LENGTH));
            structIndex += PAD_LENGTH;
            final String aTokenAddress = Numeric.prependHexPrefix(address.substring(PAD_LENGTH - ADDRESS_LENGTH));
            int skipDecimal = Numeric.toBigInt(resultData.substring(structIndex, structIndex + PAD_LENGTH)).intValue();
            int decimal = Numeric.toBigInt(resultData.substring(structIndex + skipDecimal, structIndex + skipDecimal + PAD_LENGTH)).intValue();
            int skipName = Numeric.toBigInt((resultData.substring(structIndex, structIndex + PAD_LENGTH))).intValue();
            structIndex += PAD_LENGTH;
            int nameLength = Numeric.toBigInt(resultData.substring(structIndex + skipName, structIndex + skipName + PAD_LENGTH)).intValue();
            structIndex += PAD_LENGTH;
            structIndex += skipName;
            String name = new String(HexUtil.decodeHex(resultData.substring(structIndex, structIndex + PAD_LENGTH).substring(0, nameLength * 2)), StandardCharsets.UTF_8);
            structIndex += PAD_LENGTH;
            int symbolLength = Numeric.toBigInt((resultData.substring(structIndex, structIndex + PAD_LENGTH))).intValue();
            structIndex += PAD_LENGTH;
            String symbol = new String(HexUtil.decodeHex(resultData.substring(structIndex, structIndex + PAD_LENGTH).substring(0, symbolLength * 2)), StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("decimal", decimal);
            jsonObject.put("name", name);
            jsonObject.put("address", address);
            jsonObject.put("symbol", symbol);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
