package com.pundix.core.ethereum.aave;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pundix.core.ethereum.EthereumService;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;


/**
 * Description：AaveService
 * @author joker
 * @date 2020/5/28
 */
public class AaveService extends EthereumService {
    private static AaveService inchService;

    public static AaveService getInstance() {
        if (inchService == null) {
            inchService = new AaveService();
        }
        return inchService;

    }

    public HashMap<String, String> getReserveData(String fromAddress, String ercAddress) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        AaveContract aavePoolContract = AaveContract.load(AaveContract.ProtocolDataProvider, web3j, readonlyTransactionManager, new DefaultGasProvider());
        final List<Type> resereData = aavePoolContract.getReserveData(ercAddress).send();
        HashMap<String, String> map = new HashMap<>();
        map.put("availableLiquidity", resereData.get(0).getValue().toString());
        map.put("liquidityRate", resereData.get(3).getValue().toString());
        return map;

    }

    public JSONArray getAllATokens() throws Exception {
        final AaveContract protocolDataProvider = getProtocolDataProvider(AaveContract.ProtocolDataProvider);
        final String allATokensToAbi = protocolDataProvider.getAllATokensToAbi();
        final String result = sendEthCall(AaveContract.ProtocolDataProvider, AaveContract.ProtocolDataProvider, allATokensToAbi).send().getResult();
        final JSONArray jsonArray = decodeGetAllTokenData(result);
        return jsonArray;
    }

    public JSONArray getAllReservesTokens() throws Exception {
        final AaveContract protocolDataProvider = getProtocolDataProvider(AaveContract.ProtocolDataProvider);
        final String allATokensToAbi = protocolDataProvider.getAllReservesTokens();
        final String result = sendEthCall(AaveContract.ProtocolDataProvider, AaveContract.ProtocolDataProvider, allATokensToAbi).send().getResult();
        final JSONArray jsonArray = decodeGetAllTokenData(result);
        return jsonArray;
    }

    private JSONArray decodeGetAllTokenData(String returnData) {
        int PAD_LENGTH = 64;
        int ADDRESS_LENGTH = 40;
        int index = 0;
        returnData = Numeric.cleanHexPrefix(returnData);
        final BigInteger skipLength = Numeric.toBigInt(returnData.substring(index, index + PAD_LENGTH));
        index += PAD_LENGTH;
        final BigInteger tokenArrSize = Numeric.toBigInt(returnData.substring(index, index + PAD_LENGTH));
        System.out.println("token arr size:" + tokenArrSize.intValue());
        index += PAD_LENGTH;
        JSONArray result = new JSONArray(tokenArrSize.intValue());

        BigInteger arrSkipLength;
        for (int i = 0; i < tokenArrSize.intValue(); i++) {
            arrSkipLength = Numeric.toBigInt(returnData.substring(index, index + ((i + 1) * PAD_LENGTH)));
            int structIndex = index + arrSkipLength.intValue() * 2;
            final BigInteger addressLength = Numeric.toBigInt(returnData.substring(structIndex, structIndex + PAD_LENGTH));
            structIndex += PAD_LENGTH;
            final String aTokenAddress = Numeric.prependHexPrefix(returnData.substring(structIndex, structIndex + PAD_LENGTH).substring(PAD_LENGTH - ADDRESS_LENGTH, PAD_LENGTH));
            structIndex += PAD_LENGTH;
            final BigInteger symbolLength = Numeric.toBigInt(returnData.substring(structIndex, structIndex + PAD_LENGTH));
            structIndex += PAD_LENGTH;
            final String aTokenSymbol = hexDecode(returnData.substring(structIndex, structIndex + PAD_LENGTH).substring(0, symbolLength.intValue() * 2));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("symbol", aTokenSymbol);
            jsonObject.put("address", aTokenAddress);
            result.add(jsonObject);
        }
        return result;
    }

    private String hexDecode(String hex) {
        return new String(Hex.decode(hex.getBytes()), StandardCharsets.UTF_8);
    }

    private AaveContract getProtocolDataProvider(String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        AaveContract aaveCoreContract = AaveContract.load(AaveContract.ProtocolDataProvider, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return aaveCoreContract;
    }

    public String getLendingPoolAddress(String address) throws Exception {
        AaveContract lpAddressProviderContract = getAaveProviderContract(address);
        Address lendingPoolAddress = lpAddressProviderContract.getLendingPool().send();
        return lendingPoolAddress.getValue();
    }

    private AaveContract getAaveProviderContract(String fromAdress) {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAdress);
        AaveContract aaveCoreContract = AaveContract.load(AaveContract.LendingPoolAddressesProvider, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return aaveCoreContract;
    }
}
