package com.pundix.core.binancebsc;

import android.text.TextUtils;
import com.pundix.common.utils.GsonUtils;
import com.pundix.core.ethereum.EthereumTransationData;
import com.pundix.core.ethereum.model.Gas;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Description：ETH
 *
 * @author Carl
 * @date 2020/5/26
 */
public class BinancebscTransation implements ITransation {

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
        String data;
        if (TextUtils.isEmpty(ethereumTransationData.getData())) {
            data = "0x";
        } else {
            data = ethereumTransationData.getData();
        }
        BigInteger nonce = BinancebscService.getInstance().getNonce(ethereumTransationData.getFromAddress());
        BigInteger gasPrice;
        BigInteger gasLimit;
        if (TextUtils.isEmpty(ethereumTransationData.getGasPrice())) {
            gasPrice = BinancebscService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(ethereumTransationData.getGasPrice());
        }
        if (TextUtils.isEmpty(ethereumTransationData.getGasLimit())) {
            Transaction transaction = Transaction.createFunctionCallTransaction(ethereumTransationData.getFromAddress(), nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, ethereumTransationData.getToAddress(), value, data);
            gasLimit = BinancebscService.getInstance().getGasLimit(transaction);
        } else {
            gasLimit = new BigInteger(ethereumTransationData.getGasLimit());
        }
        ((EthereumTransationData) transationData).setGasLimit(gasLimit.toString());
        ((EthereumTransationData) transationData).setGasPrice(gasPrice.toString());
        RawTransaction transaction = null;
        if (TextUtils.isEmpty(ethereumTransationData.getData())) {
            transaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, ethereumTransationData.getToAddress(), value);
        } else {
            transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ethereumTransationData.getToAddress(), value, data);
        }
        EthSendTransaction ethSendTransaction = BinancebscService.getInstance().sendRawTransaction(ethereumTransationData.getPrivateKey(), transaction);
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
    public String getBalance(String... address) {
        String balance = null;
        try {
            String address1 = address[0];
            String contract = null;
            if (address.length > 1) {
                contract = address[1];
            }
            if (TextUtils.isEmpty(contract)) {
                balance = BinancebscService.getInstance().getEthBalance(address1).toString();
            } else {
                balance = BinancebscService.getInstance().getErc20Balance(contract, address1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().toString().equals("Empty value (0x) returned from contract")) {
                balance = "0";
            }
        }
        return balance;
    }

    @Override
    public List<String> getArrayBalance(String... address) {
        return null;
    }

    @Override
    public Object getFee(TransationData transationData) throws Exception {
        EthereumTransationData ethereumTransationData = (EthereumTransationData) transationData;
        Gas gas = new Gas();
        BigInteger nonce;
        if (TextUtils.isEmpty(ethereumTransationData.getNonce())) {
            nonce = BinancebscService.getInstance().getNonce(ethereumTransationData.getFromAddress());
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

        String data;
        if (TextUtils.isEmpty(ethereumTransationData.getData())) {
            data = "0x";
        } else {
            data = ethereumTransationData.getData();
        }


        BigInteger gasPrice;
        if (TextUtils.isEmpty(ethereumTransationData.getGasPrice())) {
            gasPrice = BinancebscService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(ethereumTransationData.getGasPrice());
        }
        BigInteger gasLimit1;
        if (TextUtils.isEmpty(ethereumTransationData.getGasLimit())) {
            Transaction transaction = Transaction.createFunctionCallTransaction(ethereumTransationData.getFromAddress(), nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, ethereumTransationData.getToAddress(), value, data);
            gasLimit1 = BinancebscService.getInstance().getGasLimit(transaction);
            gasLimit1 = BinancebscService.getInstance().formatGasLimit(gasLimit1);
        } else {
            gasLimit1 = new BigInteger(ethereumTransationData.getGasLimit());
        }
        gas.setGasLimit(gasLimit1.toString());
        gas.setGasPrice(gasPrice.toString());
        return gas;
    }

    @Override
    public Object getTxs(Object data) {
        return null;
    }

}
