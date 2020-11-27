package com.pundix.core.ethereum;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.pundix.core.bitcoin.model.Gas;
import com.pundix.core.erc20.ERC20Contract;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java8.util.Optional;

/**
 * Description：EthereumTransation
 * @author Joker
 * @date 2020/5/26
 */
public class EthereumTransation implements ITransation {
    private static final String TAG = "EthereumTransation";

    public EthereumTransation(String url) {
        EthereumService.getInstance().initUrl(url);
    }

    @Override
    public TransationResult sendTransation(TransationData data) throws IOException {
        TransationResult resp = new TransationResult();
        BigInteger value;
        if (TextUtils.isEmpty(data.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(data.getValue());
        }
        BigInteger nonce = EthereumService.getInstance().getNonce(data.getFromAddress());
        BigInteger gasPrice;
        BigInteger gasLimit;
        if (TextUtils.isEmpty(data.getGasLimit()) || TextUtils.isEmpty(data.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
            gasLimit = EthereumService.getInstance().getGasLimit(data.getFromAddress(), data.getToAddress(), gasPrice, value);
            formatGasLimit(gasLimit);

        } else {
            gasPrice = new BigInteger(data.getGasPrice());
            gasLimit = new BigInteger(data.getGasLimit());
        }

        RawTransaction transaction = null;
        if (TextUtils.isEmpty(data.getData())) {
            transaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, data.getToAddress(), value);
        } else {
            transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, data.getToAddress(), value, data.getData());
        }

        EthSendTransaction ethSendTransaction = EthereumService.getInstance().sendRawTransaction(data.getPrivateKey(), transaction);
        if (!TextUtils.isEmpty(ethSendTransaction.getTransactionHash())) {
            resp.setCode(0);
            resp.setHash(ethSendTransaction.getTransactionHash());
        } else {
            resp.setCode(ethSendTransaction.getError().getCode());
            resp.setMsg(ethSendTransaction.getError().getMessage());
        }
        return resp;

    }

    public TransationResult onSendTransactionWalletConnect(TransationData data) throws IOException {
        TransationResult resp = new TransationResult();
        BigInteger nonce = null;
        if (TextUtils.isEmpty(data.getNonce())) {
            nonce = EthereumService.getInstance().getNonce(data.getFromAddress());
        } else {
            nonce = new BigInteger(data.getNonce());
        }
        BigInteger value = null;
        if (TextUtils.isEmpty(data.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(data.getValue());
        }

        BigInteger gasPrice = null;
        if (TextUtils.isEmpty(data.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(data.getGasPrice());
        }

        BigInteger gasLimit = null;
        if (!TextUtils.isEmpty(data.getGasLimit())) {
            gasLimit = new BigInteger(data.getGasLimit());
        } else {
            gasLimit = formatGasLimit(gasLimit);
        }
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, data.getToAddress(), value, data.getData());
        EthSendTransaction ethSendTransaction = EthereumService.getInstance().sendRawTransaction(data.getPrivateKey(), rawTransaction);
        if (ethSendTransaction == null) {
        }
        if (!TextUtils.isEmpty(ethSendTransaction.getTransactionHash())) {
            resp.setCode(0);
            resp.setHash(ethSendTransaction.getTransactionHash());
        } else {
            resp.setCode(ethSendTransaction.getError().getCode());
            resp.setMsg(ethSendTransaction.getError().getMessage());
        }
        return resp;
    }

    public TransactionReceipt getTransactionReceipt(String hash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = EthereumService.getInstance().getTransactionReceipt(hash);
        Optional<TransactionReceipt> transactionReceipt1 = transactionReceipt.getTransactionReceipt();
        return transactionReceipt1.get();
    }

    public Disposable getEstimateGas(TransationData data, Consumer<Gas> onSuccess, Consumer<Throwable> onError) {
        Disposable disposable = Observable.fromCallable(() -> getEstimateGas(data)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess, onError);
        return disposable;
    }

    public Gas getEstimateGas(TransationData data) throws IOException {
        Gas gasModel = new Gas();
        BigInteger nonce;
        if (TextUtils.isEmpty(data.getNonce())) {
            nonce = EthereumService.getInstance().getNonce(data.getFromAddress());
        } else {
            nonce = new BigInteger(data.getNonce());
        }
        data.setNonce(nonce.toString());
        BigInteger value;
        if (TextUtils.isEmpty(data.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(data.getValue());
        }
        data.setValue(value.toString());
        BigInteger gasPrice;
        if (TextUtils.isEmpty(data.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(data.getGasPrice());
        }
        data.setGasPrice(gasPrice.toString());

        BigInteger gasLimit1;
        if (TextUtils.isEmpty(data.getData()) || data.getData().equals("0x")) {
            gasLimit1 = new BigInteger("21000");
        } else {
            Transaction transaction = Transaction.createFunctionCallTransaction(data.getFromAddress(), nonce, null, null, data.getToAddress(), value, data.getData());
            gasLimit1 = EthereumService.getInstance().getGasLimit(transaction);
            Log.e(TAG, "getEstimateGas: " + gasLimit1);
        }

        gasModel.setGasLimit(formatGasLimit(gasLimit1).toString());
        gasModel.setGasPrice(gasPrice.toString());
        return gasModel;
    }

    public Disposable approve(TransationData transationData, String tokenContractAddress, Consumer<TransactionReceipt> onSuccess, Consumer<Throwable> onError) {
        Disposable disposable = Observable.fromCallable(() -> {
            final TransactionReceipt transactionReceipt = EthereumService.getInstance().approve(transationData.getPrivateKey(), tokenContractAddress, transationData.getToAddress(), transationData.getValue(), transationData.getGasPrice(), transationData.getGasLimit());
            return transactionReceipt;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess, onError);
        return disposable;
    }

    public String getErc20Name(String contractAddress, String address) throws Exception {
        return EthereumService.getInstance().getErc20Name(contractAddress, address);
    }

    public int getErc20Decimals(String contractAddress, String address) throws Exception {
        return EthereumService.getInstance().getErc20Decimals(contractAddress, address);
    }

    public Disposable allowance(TransationData transationData, String tokenContractAddress, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        Disposable disposable = Observable.fromCallable(() -> {
            String amount = EthereumService.getInstance().allowance(transationData.getFromAddress(), transationData.getToAddress(), tokenContractAddress);
            return amount;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess, onError);
        return disposable;
    }

    public String allowance(TransationData transationData, String tokenContractAddress) throws Exception {
        String amount = EthereumService.getInstance().allowance(transationData.getFromAddress(), transationData.getToAddress(), tokenContractAddress);
        return amount;
    }


    /**
     * @param data
     * @return
     * @throws IOException
     */
    public TransationResult SignTransaction(TransationData data) throws IOException {
        TransationResult resp = new TransationResult();
        BigInteger nonce = null;
        if (TextUtils.isEmpty(data.getNonce())) {
            nonce = EthereumService.getInstance().getNonce(data.getFromAddress());
        } else {
            nonce = new BigInteger(data.getNonce());
        }
        BigInteger value;
        if (TextUtils.isEmpty(data.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(data.getValue());
        }

        BigInteger gasPrice = null;
        if (TextUtils.isEmpty(data.getGasPrice())) {
            gasPrice = EthereumService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(data.getGasPrice());
        }
        BigInteger gasLimit = null;
        if (TextUtils.isEmpty(data.getGasLimit())) {
            gasLimit = new BigInteger("60000");
        } else {
            gasLimit = new BigInteger(data.getGasLimit());
        }
        try {
            byte[] bytes = encodeEthereumData(nonce, gasPrice, gasLimit, data.getToAddress(), value, null);
            Sign.SignatureData signMessage = Sign.signMessage(bytes, Credentials.create(data.getPrivateKey()).getEcKeyPair(), true);
            byte[] bArrx = new byte[65];
            System.arraycopy(signMessage.getR(), 0, bArrx, 0, 32);
            System.arraycopy(signMessage.getS(), 0, bArrx, 32, 32);
            bArrx[64] = (byte) (signMessage.getV() - 27);
            byte[] send = encodeEthereumData(nonce, gasPrice, gasLimit, data.getToAddress(), value, bArrx);
            resp.setHash(Numeric.toHexString(send));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setHash("0x0");
        }
        return resp;
    }

    public TransationResult sendRawTransaction(String data) throws IOException {
        TransationResult resp = new TransationResult();
        final EthSendTransaction ethSendTransaction = EthereumService.getInstance().sendRawTransaction(data);
        resp.setHash(ethSendTransaction.getTransactionHash());
        return resp;
    }


    public byte[] encodeEthereumData(BigInteger nonce, BigInteger priceFee, BigInteger limit, String toAddress, BigInteger weiAmout, byte[] bArr) throws Exception {
        int chainId = 1;

        byte[] bArr2;
        byte[] bArr3;
        if (bArr == null) {
            bArr = BigInteger.valueOf((long) chainId).toByteArray();
            bArr2 = new byte[0];
            bArr3 = new byte[0];
        } else {
            if (chainId != 0) {
                bArr2 = BigInteger.valueOf((long) (((bArr[64] + 35) + chainId) + chainId)).toByteArray();
            } else {
                bArr2 = BigInteger.valueOf((long) (bArr[64] + 27)).toByteArray();
            }
            byte[] copyOfRange = Arrays.copyOfRange(bArr, 0, 32);
            bArr3 = Arrays.copyOfRange(bArr, 32, 64);
            bArr = bArr2;
            bArr2 = copyOfRange;
        }

        return RlpEncoder.encode(new RlpList(asRlpValues(nonce, priceFee, limit, toAddress, weiAmout, bArr, bArr2, bArr3)));
    }

    private static List<RlpType> asRlpValues(BigInteger nonce, BigInteger priceFee, BigInteger limit, String toAddress, BigInteger weiAmout, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(RlpString.create(nonce));
        arrayList.add(RlpString.create(priceFee));
        arrayList.add(RlpString.create(limit));
        arrayList.add(RlpString.create(Numeric.hexStringToByteArray(toAddress)));
        arrayList.add(RlpString.create(weiAmout));
        byte[] data = new byte[0];
        arrayList.add(RlpString.create(data));
        arrayList.add(RlpString.create(Bytes.trimLeadingZeroes(bArr)));
        arrayList.add(RlpString.create(Bytes.trimLeadingZeroes(bArr2)));
        arrayList.add(RlpString.create(Bytes.trimLeadingZeroes(bArr3)));
        return arrayList;
    }

    @Override
    public String getBalance(String address) throws Exception {
        BigInteger accountBalance = EthereumService.getInstance().getEthBalance(address);
        return accountBalance.toString();
    }

    public String getBalance(String contractAddress, String address) throws Exception {
        String erc20Balance = EthereumService.getInstance().getErc20Balance(contractAddress, address);
        return erc20Balance;

    }

    private BigInteger formatGasLimit(BigInteger gasLimit) {
        if (gasLimit == null || gasLimit.compareTo(new BigInteger("60000")) < 0) {
            return new BigInteger("60000");
        }
        return gasLimit;
    }

}
