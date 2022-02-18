package com.pundix.core.binancebsc;


import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.pundix.common.http.exception.ServerException;
import com.pundix.common.utils.RxUtils;
import com.pundix.core.FunctionxNodeConfig;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.EthereumTransationData;
import com.pundix.core.ethereum.contract.ERC20Contract;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Description：BinancebscService
 * @author CY
 * @date 2020/5/28
 */

public class BinancebscService {
    private static final String TAG = "EthereumService";
    private static BinancebscService erc20RpcService;
    public Web3j web3j;

    public static BinancebscService getInstance() {
        if (erc20RpcService == null) {
            erc20RpcService = new BinancebscService();
        }
        return erc20RpcService;
    }

    public BinancebscService() {
        web3j = Web3j.build(new HttpService(Coin.BINANCE_BSC.getNodeUrl()));
    }

    public void changeUrl() {
        web3j = Web3j.build(new HttpService(Coin.BINANCE_BSC.getNodeUrl()));
    }

    public String getErc20Balance(String contractAddress, String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.balanceOf(address).send().toString();
    }

    public String getErc20Name(String contractAddress, String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.name().send();
    }

    public int getErc20Decimals(String contractAddress, String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.decimals().send().intValue();
    }

    public String getErc20Symbol(String contractAddress, String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.symbol().send();
    }

    public String allowance(String fromAddress, String to, String contractAddress) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.allowance(fromAddress, to).send().toString();
    }

    public Disposable allowance(EthereumTransationData transationData, String tokenContractAddress, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        Disposable disposable = Observable.fromCallable(() -> {
            String amount = allowance(transationData.getFromAddress(), transationData.getToAddress(), tokenContractAddress);
            return amount;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess, onError);
        return disposable;
    }

    public TransactionReceipt approve(String privateKey, String contractAddress, String toAddress, String values, String gasPrice, String gasLimit) throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, credentials, new StaticGasProvider(new BigInteger(gasPrice), new BigInteger(gasLimit)));
        final TransactionReceipt receipt = erc20Contract.approve(toAddress, new BigInteger(values)).send();
        return receipt;
    }

    public Disposable approve(TransationData transationData, Consumer<TransactionReceipt> onSuccess, Consumer<Throwable> onError) {
        Disposable disposable = Observable.fromCallable(() -> {
            EthereumTransationData ethereumTransationData = (EthereumTransationData) transationData;
            final TransactionReceipt transactionReceipt = approve(ethereumTransationData.getPrivateKey(), ethereumTransationData.getFromAddress(), ethereumTransationData.getToAddress(), ethereumTransationData.getValue(), ethereumTransationData.getGasPrice(), ethereumTransationData.getGasLimit());
            return transactionReceipt;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess, onError);
        return disposable;
    }

    public BigInteger getEthBalance(String address) throws IOException {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    public Observable<Object> ethGetCode(String contractAddress) {
        return Observable.create(emitter -> {
            String ethGetCode = web3j.ethGetCode(contractAddress, DefaultBlockParameterName.LATEST).send().getCode();
            if (TextUtils.isEmpty(ethGetCode)) {
                emitter.onError(new ServerException("Sorry! Failed to query contract creation code for the currency contract", 202128));
                emitter.onComplete();
                return;
            }
            byte[] bytes = Numeric.hexStringToByteArray(ethGetCode);
            byte[] resultCodeHash = Sha256Hash.hash(bytes);
            emitter.onNext(Base64.encodeToString(resultCodeHash, Base64.NO_WRAP));
            emitter.onComplete();
        }).compose(RxUtils.rxSchedulerHelper());
    }

    public Observable<Object> ethGetBlockByNumber(BigInteger blockNumber, String serverHash) {
        return Observable.fromCallable((Callable<Object>) () -> {
            String hash = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false)
                    .send()
                    .getBlock()
                    .getHash();
            return serverHash.equals(hash);
        }).compose(RxUtils.rxSchedulerHelper());
    }

    public String getEthTransferEncoder(String toAddress, BigInteger amount) {
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(Keys.toChecksumAddress(toAddress)),
                new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        String abi = FunctionEncoder.encode(function);
        return abi;
    }

    public EthSendTransaction sendRawTransaction(String privateKey, RawTransaction rawTransaction) throws IOException {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(privateKey));
        return web3j.ethSendRawTransaction(Numeric.toHexString(signedMessage)).send();
    }

    public TransationResult sendRawTransaction(String data) throws IOException {
        TransationResult resp = new TransationResult();
        final EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(data).send();
        resp.setHash(ethSendTransaction.getTransactionHash());
        return resp;
    }

    public TransactionReceipt getTransactionReceipt(String hash) throws IOException {
        return web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt().get();
    }

    public BigInteger getGasLimit(String fromAddress, String toAddress, String amount, BigInteger gasPrice, String contractAddress) throws IOException {
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(Keys.toChecksumAddress(toAddress)),
                new Uint256(new BigInteger(amount))),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        Transaction transaction = new Transaction(fromAddress, getNonce(fromAddress), gasPrice, DefaultGasProvider.GAS_LIMIT, contractAddress, null, encodedFunction);
        return web3j.ethEstimateGas(transaction).send().getAmountUsed();
    }

    public BigInteger getGasLimit(String fromAddress, String toAddress, BigInteger gasPrice, BigInteger amount) throws IOException {
        return web3j.ethEstimateGas(Transaction.createEtherTransaction(fromAddress, getNonce(fromAddress), gasPrice, DefaultGasProvider.GAS_LIMIT, toAddress, amount)).send().getAmountUsed();
    }

    public BigInteger getGasLimit(Transaction transaction) throws IOException {
        return web3j.ethEstimateGas(transaction).send().getAmountUsed();
    }

    public BigInteger getNonce(String fromAddress) throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }

    public BigInteger getGasPrice() throws IOException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        return gasPrice;
    }

    public TransationResult onSendTransactionWalletConnect(TransationData data) throws IOException {
        EthereumTransationData ethereumTransationData = (EthereumTransationData) data;
        TransationResult resp = new TransationResult();
        BigInteger nonce;
        if (TextUtils.isEmpty(ethereumTransationData.getNonce())) {
            nonce = BinancebscService.getInstance().getNonce(ethereumTransationData.getFromAddress());
        } else {
            nonce = new BigInteger(ethereumTransationData.getNonce());
        }
        BigInteger value;
        if (TextUtils.isEmpty(ethereumTransationData.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(ethereumTransationData.getValue());
        }

        BigInteger gasPrice;
        if (TextUtils.isEmpty(ethereumTransationData.getGasPrice())) {
            gasPrice = BinancebscService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(ethereumTransationData.getGasPrice());
        }

        BigInteger gasLimit = null;
        if (!TextUtils.isEmpty(ethereumTransationData.getGasLimit())) {
            gasLimit = new BigInteger(ethereumTransationData.getGasLimit());
        } else {
            gasLimit = formatGasLimit(gasLimit);
        }
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ethereumTransationData.getToAddress(), value, ethereumTransationData.getData());
        EthSendTransaction ethSendTransaction = BinancebscService.getInstance().sendRawTransaction(ethereumTransationData.getPrivateKey(), rawTransaction);

        if (!TextUtils.isEmpty(ethSendTransaction.getTransactionHash())) {
            resp.setCode(0);
            resp.setHash(ethSendTransaction.getTransactionHash());
        } else {
            resp.setCode(ethSendTransaction.getError().getCode());
            resp.setMsg(ethSendTransaction.getError().getMessage());
        }
        return resp;
    }

    public TransationResult SignTransaction(EthereumTransationData data) throws IOException {
        TransationResult resp = new TransationResult();
        BigInteger nonce;
        if (TextUtils.isEmpty(data.getNonce())) {
            nonce = BinancebscService.getInstance().getNonce(data.getFromAddress());
        } else {
            nonce = new BigInteger(data.getNonce());
        }
        BigInteger value;
        if (TextUtils.isEmpty(data.getValue())) {
            value = BigInteger.ZERO;
        } else {
            value = new BigInteger(data.getValue());
        }
        BigInteger gasPrice;
        if (TextUtils.isEmpty(data.getGasPrice())) {
            gasPrice = BinancebscService.getInstance().getGasPrice();
        } else {
            gasPrice = new BigInteger(data.getGasPrice());
        }
        BigInteger gasLimit;
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
            bArrx[64] = (byte) (signMessage.getV()[0] - 27);
            byte[] send = encodeEthereumData(nonce, gasPrice, gasLimit, data.getToAddress(), value, bArrx);
            resp.setHash(Numeric.toHexString(send));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setHash("0x0");
        }
        return resp;
    }


    public byte[] encodeEthereumData(BigInteger nonce, BigInteger priceFee, BigInteger limit, String toAddress, BigInteger weiAmout, byte[] bArr) throws Exception {
        int chainId = Integer.parseInt(FunctionxNodeConfig.getInstance().getNodeConfig(Coin.BINANCE_BSC).getChainId());

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

    public BigInteger formatGasLimit(BigInteger gasLimit) {
        if (gasLimit.compareTo(new BigInteger("100000")) > 0) {
            gasLimit = new BigDecimal(gasLimit).multiply(new BigDecimal("1.2")).toBigInteger();
        }
        return gasLimit;
    }

    public Request<?, EthCall> sendEthCall(String from, String to, String inputData) {
        return web3j.ethCall(Transaction.createEthCallTransaction(from, to, inputData), DefaultBlockParameterName.LATEST);
    }

}
