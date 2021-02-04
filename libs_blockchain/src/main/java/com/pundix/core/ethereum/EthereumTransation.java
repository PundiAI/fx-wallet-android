package com.pundix.core.ethereum;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.pundix.common.utils.GsonUtils;
import com.pundix.common.utils.Logs;
import com.pundix.core.ethereum.model.Gas;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Description：EthereumTransation
 * @author joker
 * @date 2020/5/26
 */
public class EthereumTransation implements ITransation {

    @Override
    public TransationResult sendTransation(TransationData transationData) throws IOException {
        EthereumTransationData ethereumTransationData = (EthereumTransationData) transationData;
        TransationResult resp = new TransationResult();
        BigInteger value;
        if (TextUtils.isEmpty(ethereumTransationData.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(ethereumTransationData.getValue());
        }
        BigInteger nonce = EthereumService.getInstance().getNonce(ethereumTransationData.getFromAddress());
        BigInteger gasPrice;
        BigInteger gasLimit;
        if (TextUtils.isEmpty(ethereumTransationData.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(ethereumTransationData.getGasPrice());
        }

        if (TextUtils.isEmpty(ethereumTransationData.getData()) || ethereumTransationData.getData().equals("0x")) {
            gasLimit = new BigInteger("21000");
        } else {
            Transaction transaction = Transaction.createFunctionCallTransaction(ethereumTransationData.getFromAddress(), nonce, null, null, ethereumTransationData.getToAddress(), value, ethereumTransationData.getData());
            gasLimit = EthereumService.getInstance().getGasLimit(transaction);
        }
        ((EthereumTransationData) transationData).setGasLimit(gasLimit.toString());
        ((EthereumTransationData) transationData).setGasPrice(gasPrice.toString());
        RawTransaction transaction = null;
        if (TextUtils.isEmpty(ethereumTransationData.getData())) {
            transaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, ethereumTransationData.getToAddress(), value);
        } else {
            transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ethereumTransationData.getToAddress(), value, ethereumTransationData.getData());
        }

        EthSendTransaction ethSendTransaction = EthereumService.getInstance().sendRawTransaction(ethereumTransationData.getPrivateKey(), transaction);
        if (!TextUtils.isEmpty(ethSendTransaction.getTransactionHash())) {
            resp.setCode(0);
            resp.setHash(ethSendTransaction.getTransactionHash());
        } else {
            resp.setCode(ethSendTransaction.getError().getCode());
            resp.setMsg(ethSendTransaction.getError().getMessage());
        }
        return resp;

    }

    @Override
    public String getBalance(String address) throws Exception {
        return "";
    }


    @Override
    public String getBalance(String... address)   {
        String balance = null;
        try {
            String address1 = address[0];
            String contract = null;
            if (address.length > 1) {
                contract = address[1];
            }
            if (TextUtils.isEmpty(contract)) {
                balance= EthereumService.getInstance().getEthBalance(address1).toString();
            }else {
                balance= EthereumService.getInstance().getErc20Balance(contract, address1);
            }
        }catch (Exception e){
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().toString().equals("Empty value (0x) returned from contract")) {
                balance = "0";
            }
        }
        return balance;
    }

    @Override
    public List<String> getArrayBalance(String... address)  {
        throw new RuntimeException();
    }


    @Override
    public Object getFee(TransationData transationData) throws Exception {
        EthereumTransationData ethereumTransationData = (EthereumTransationData) transationData;
        Gas gas = new Gas();
        BigInteger nonce;
        if (TextUtils.isEmpty(ethereumTransationData.getNonce())) {
            nonce = EthereumService.getInstance().getNonce(ethereumTransationData.getFromAddress());
        } else {
            nonce = new BigInteger(ethereumTransationData.getNonce());
        }
        ethereumTransationData.setNonce(nonce.toString());
        BigInteger value;
        if (TextUtils.isEmpty(ethereumTransationData.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(ethereumTransationData.getValue());
        }
        ethereumTransationData.setValue(value.toString());
        BigInteger gasPrice;
        if (TextUtils.isEmpty(ethereumTransationData.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(ethereumTransationData.getGasPrice());
        }
        ethereumTransationData.setGasPrice(gasPrice.toString());
        BigInteger gasLimit1;
        if (TextUtils.isEmpty(ethereumTransationData.getData()) || ethereumTransationData.getData().equals("0x")) {
            gasLimit1 = new BigInteger("21000");
            gas.setGasLimit(gasLimit1.toString());
        } else {
            Transaction transaction = Transaction.createFunctionCallTransaction(ethereumTransationData.getFromAddress(), nonce, null, null, ethereumTransationData.getToAddress(), value, ethereumTransationData.getData());
            gasLimit1 = EthereumService.getInstance().getGasLimit(transaction);
            gas.setGasLimit(EthereumService.getInstance().formatGasLimit(gasLimit1).toString());
        }
        gas.setGasPrice(gasPrice.toString());

        return gas;
    }


}
