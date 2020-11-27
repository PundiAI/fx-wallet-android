package com.pundix.core.ethereum;


import com.pundix.core.erc20.ERC20Contract;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;


/**
 * Description：EthereumService
 * @author Joker
 * @date 2020/5/28
 */
public class EthereumService {
    private static EthereumService erc20RpcService;
    private static Web3j web3j;

    public static EthereumService getInstance() {
        if (erc20RpcService == null) {
            erc20RpcService = new EthereumService();
        }
        return erc20RpcService;

    }

    public void initUrl(String url) {
        web3j = Web3j.build(new HttpService(url));
    }


    public String getErc20Balance(String contractAddress, String address) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.balanceOf(address).send().toString();
    }


    /**
     * getErc20Name
     * @param contractAddress
     * @param address
     * @return
     * @throws Exception
     */
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


    /**
     * allowance
     * @param contractAddress
     * @param to
     * @param fromAddress
     * @return
     * @throws Exception
     */
    public String allowance(String fromAddress,String to,String contractAddress) throws Exception {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAddress);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return erc20Contract.allowance(fromAddress,to).send().toString();
    }

    /**
     * approve
     * @param privateKey
     * @param contractAddress
     * @param values
     * @param gasPrice
     * @param gasLimit
     * @return
     * @throws Exception
     */
    public TransactionReceipt approve(String privateKey, String contractAddress,String toAddress, String values, String gasPrice, String gasLimit) throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        ERC20Contract erc20Contract = ERC20Contract.load(contractAddress, web3j, credentials, new StaticGasProvider(new BigInteger(gasPrice), new BigInteger(gasLimit)));
        final TransactionReceipt receipt = erc20Contract.approve(toAddress, new BigInteger(values)).send();
        return receipt;
    }



    /**
     * getEthBalance
     *
     * @param address
     * @return
     * @throws IOException
     */
    public BigInteger getEthBalance(String address) throws IOException {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }


    /**
     * getEthTransferEncoder
     *
     * @param toAddress
     * @param amount
     * @return
     */
    public String getEthTransferEncoder(String toAddress, BigInteger amount) {
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(Keys.toChecksumAddress(toAddress)),

                new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        String abi = FunctionEncoder.encode(function);
        return abi;
    }


    /**
     * sendRawTransaction
     *
     * @param privateKey
     * @param rawTransaction
     * @return
     * @throws IOException
     */
    public EthSendTransaction sendRawTransaction(String privateKey, RawTransaction rawTransaction) throws IOException {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(privateKey));
        return web3j.ethSendRawTransaction(Numeric.toHexString(signedMessage)).send();
    }


    public EthSendTransaction sendRawTransaction(String rawTransaction) throws IOException {
        return web3j.ethSendRawTransaction(rawTransaction).send();
    }


    /**
     * getTransactionReceipt
     * @param hash
     * @return
     * @throws IOException
     */
    public EthGetTransactionReceipt getTransactionReceipt(String hash) throws IOException {
        return web3j.ethGetTransactionReceipt(hash).send();
    }


    /**
     * getGasLimit
     *
     * @param fromAddress
     * @return
     * @throws IOException
     */
    public BigInteger getGasLimit(String fromAddress, String toAddress, String amount, BigInteger gasPrice, String contractAddress) throws IOException {
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(Keys.toChecksumAddress(toAddress)),
                new Uint256(new BigInteger(amount))),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        Transaction transaction = new Transaction(fromAddress, getNonce(fromAddress), gasPrice, DefaultGasProvider.GAS_LIMIT, contractAddress, null, encodedFunction);
        return web3j.ethEstimateGas(transaction).send().getAmountUsed();
    }

    /**
     * getGasLimit
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     * @throws IOException
     */
    public BigInteger getGasLimit(String fromAddress, String toAddress, BigInteger gasPrice, BigInteger amount) throws IOException {
        return web3j.ethEstimateGas(Transaction.createEtherTransaction(fromAddress, getNonce(fromAddress), gasPrice, DefaultGasProvider.GAS_LIMIT, toAddress, amount)).send().getAmountUsed();
    }


    public BigInteger getGasLimit(Transaction transaction) throws IOException {
        return web3j.ethEstimateGas(transaction).send().getAmountUsed();
    }


    /**
     * Nonce
     *
     * @param fromAddress
     * @return
     */
    public BigInteger getNonce(String fromAddress) throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }

    /**
     * getGasPrice
     *
     * @return
     */
    public BigInteger getGasPrice() throws IOException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        return gasPrice;
    }
}
