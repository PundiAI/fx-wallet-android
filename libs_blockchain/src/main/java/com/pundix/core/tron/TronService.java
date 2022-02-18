package com.pundix.core.tron;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.pundix.common.utils.ByteUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.contract.FuncitonxBridgeContract;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.tron.contract.TronContract;

import org.bouncycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.Return.response_code;
import org.tron.common.utils.AddressHelper;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Account;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.walletserver.GrpcClient;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


public class TronService {
    public static Map<String, TronService> clientMap;
    private GrpcClient grpcClient;

    public static TronService getInstance() {
        if (clientMap == null) {
            clientMap = new HashMap<>();
        }
        final String nodeUrl = Coin.TRON.getNodeUrl();
        TronService tronService = clientMap.get(nodeUrl);
        if (tronService == null) {
            tronService = new TronService(nodeUrl);
            clientMap.put(nodeUrl, tronService);
        }
        return tronService;
    }

    public TronService(String url) {
        grpcClient = new GrpcClient(url);
    }

    public Account getBanlance(String address) {
        final Account account = grpcClient.queryAccount(AddressHelper.decodeFromBase58Check(address));
        return account;
    }

    public Protocol.Transaction getTransactionById(String txid) {
        return grpcClient.getTransactionById(txid);
    }

    public Protocol.TransactionInfo getTransactionInfoById(String txid) {
        return grpcClient.getTransactionInfoById(txid);
    }


    public String getTrc10Banlance(String address, String assetId) {
        final Account account = grpcClient.queryAccount(AddressHelper.decodeFromBase58Check(address));
        final Long aLong = account.getAssetV2Map().get(assetId);
        return aLong.toString();
    }

    public Protocol.Block getBlock(long blockNum) {
        return grpcClient.getBlock(blockNum);
    }

    public GrpcAPI.AccountResourceMessage getAccountResourceMessage(String address) {
        return grpcClient.getAccountResource(AddressHelper.decodeFromBase58Check(address));
    }


    public TransationResult transfer(Protocol.Transaction signaturedTransaction) {
        int i = 3;
        GrpcAPI.Return response = grpcClient.broadcastTransaction(signaturedTransaction);
        while (response.getResult() == false && response.getCode() == response_code.SERVER_BUSY
                && i > 0) {
            i--;
            response = grpcClient.broadcastTransaction(signaturedTransaction);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TransationResult transationResult = new TransationResult();
        if (response.getResult() == false) {
            transationResult.setCode(-1);
            transationResult.setMsg(response.getMessage().toStringUtf8());
        } else {
            transationResult.setCode(0);
        }
        return transationResult;
    }


    public GrpcAPI.TransactionExtention getTrc20Balance(String address, String contractAddress) {
        String contractTrigger = TronContract.balanceOfToAbi(address);
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(address, contractAddress, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerContract(triggerSmartContract);
        return triggerContract;
    }

    public String getTrc20Name(String address, String contractAddress) throws IOException {
        String contractTrigger = TronContract.nameOfToAbi();
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(address, contractAddress, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerConstantContract(triggerSmartContract);
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            return parseString(triggerContract.getConstantResult(0).toByteArray());
        }
        throw new IOException("not found name");
    }

    private String parseString(byte[] data) {
        final String dataHex = ByteUtils.toHexString(data);
        final int skipStart = Numeric.toBigInt(dataHex.substring(0, 64)).intValue();
        final int length = Numeric.toBigInt(dataHex.substring(64, 128)).intValue();
        int start = 64 + skipStart * 2;
        String name = new String(ByteUtils.hexStringToByteArray(dataHex.substring(start, start + length * 2)));
        return name;
    }

    public String getTrc20Symbol(String address, String contractAddress) throws IOException {
        String contractTrigger = TronContract.symbolOfToAbi();
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(address, contractAddress, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerConstantContract(triggerSmartContract);
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            return parseString(triggerContract.getConstantResult(0).toByteArray());
        }
        throw new IOException("Not found symbol");
    }

    public int getTrc20Decimals(String address, String contractAddress) throws IOException {
        String contractTrigger = TronContract.decimalsOfToAbi();
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(address, contractAddress, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerContract(triggerSmartContract);
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            return Numeric.toBigInt(ByteUtils.toHexString(triggerContract.getConstantResult(0).toByteArray())).intValue();
        }
        throw new IOException("Not found decimals");
    }


    public long getEnergyUsed(TronTransationData tronTransation) {
        SmartContractOuterClass.TriggerSmartContract.Builder transferContractBuilder = SmartContractOuterClass.TriggerSmartContract.newBuilder();
        transferContractBuilder.setOwnerAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getFrom())));
        transferContractBuilder.setContractAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getTo())));
        transferContractBuilder.setCallValue(0L);
        byte[] input = Hex.decode(tronTransation.getData());
        transferContractBuilder.setData(ByteString.copyFrom(input));
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerConstantContract(transferContractBuilder.build());
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            return triggerContract.getEnergyUsed();
        }
        return 0;
    }

    public String allowance(String approveContractAddress, String address, String tokenAdress) {
        String contractTrigger = TronContract.allowanceToAbi(address, approveContractAddress);
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(address, tokenAdress, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerContract(triggerSmartContract);
        String value = "0";
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            value = new BigInteger(ByteUtils.toHexString(triggerContract.getConstantResult(0).toByteArray()), 16).toString();
        }
        return value;
    }

    private SmartContractOuterClass.TriggerSmartContract creatSmartContractOuterClass(String ownerAddress, String contractAddress, String data) {
        final SmartContractOuterClass.TriggerSmartContract.Builder builder = SmartContractOuterClass.TriggerSmartContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(ownerAddress)));
        builder.setContractAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(contractAddress)));
        builder.setData(ByteString.copyFrom(Hex.decode(data)));
        builder.setCallValue(0L);
        return builder.build();
    }


    public boolean checkAssetStatus(String contractAddress) {
        String bridgeContract = FuncitonxBridgeContract.getManagerBridgeContract(Coin.TRON);
        String contractTrigger = TronContract.checkAssetStatusToAbi(contractAddress);
        final SmartContractOuterClass.TriggerSmartContract triggerSmartContract = creatSmartContractOuterClass(bridgeContract, bridgeContract, contractTrigger);
        final GrpcAPI.TransactionExtention triggerContract = grpcClient.triggerConstantContract(triggerSmartContract);
        if (triggerContract != null && triggerContract.getResult().getResult()) {
            return (Boolean) FunctionReturnDecoder.decodeIndexedValue(ByteUtils.toHexString(triggerContract.getConstantResult(0).toByteArray()), new TypeReference<Bool>() {
            }).getValue();
        }
        return false;
    }


}
