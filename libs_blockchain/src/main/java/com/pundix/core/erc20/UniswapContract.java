package com.pundix.core.erc20;


import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOError;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class UniswapContract   {

    private volatile static UniswapContract mUniswapContract;
    public static final String FACTORY_ADDRESS = "0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f";
    public static final String ROUTER_ADDRESS = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";

    private static final BigInteger GAS_LIMIT_FOR_CONSTANT_CALLS = new BigInteger("250000");
    protected final DefaultBlockParameter defaultBlockParameter = DefaultBlockParameterName.LATEST;

    private static final String FUNC_GETPAIR = "getPair";
    private static final String FUNC_TOKEN0 = "token0";
    private static final String FUNC_TOKEN1 = "token1";
    private static final String FUNC_GETRESERVES = "getReserves";
    private static final String FUNC_APPROVE = "approve";
    private static final String FUNC_GETAMOUNTIN = "getAmountsIn";
    private static final String FUNC_GETAMOUNTOUT = "getAmountsOut";
    private static final String FUNC_WETH = "WETH";
    private static final String FUNC_ALLPAIRS = "allPairs";
    private static final String FUNC_ALLPAIRSLENGTH = "allPairsLength";
    private static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_SWAPTOKENSFOREXACTETH = "swapTokensForExactETH";
    public static final String FUNC_SWAPEXACTTOKENSFORETH = "swapExactTokensForETH";
    public static final String FUNC_SWAPETHFOREXACTTOKENS = "swapETHForExactTokens";
    public static final String FUNC_SWAPEXACTETHFORTOKENS = "swapExactETHForTokens";
    public static final String FUNC_SWAPEXACTTOKENSFORTOKENS = "swapExactTokensForTokens";
    public static final String FUNC_SWAPTOKENSFOREXACTTOKENS = "swapTokensForExactTokens";

    protected Web3j web3j;

    public static UniswapContract getInstance(String url){
        if(mUniswapContract==null){
            synchronized (UniswapContract.class) {
                if(mUniswapContract==null){
                    mUniswapContract =new UniswapContract(url);
                }
            }
        }
        return  mUniswapContract;
    }


    private UniswapContract(String url) {
        this.web3j = Web3j.build(new HttpService(url));
    }


    private String EthCallConstantCall(String address, String contractAddress, Function function){
        String encodedFunction = FunctionEncoder.encode(function);
        String value="";
        try {
            org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                            address, contractAddress, encodedFunction),
                    defaultBlockParameter)
                    .send();
            value = ethCall.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  value;
    }

    public BigInteger getNonce(String currentAddress) {
        try {
            return web3j.ethGetTransactionCount(currentAddress, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private EthSendTransaction submit(Credentials credentials, String address, BigInteger value, String data, BigInteger nonce,BigInteger gasPrice, BigInteger gasLimit){
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, address, value, data);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        try {
            return web3j.ethSendRawTransaction(Numeric.toHexString(signedMessage)).send();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public BigInteger getGasPrice() {
        try {
            return web3j.ethGasPrice().send().getGasPrice();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }


    public  List<Type> getPair(String tokenA, String tokenB) {

        final Function function = new Function(
                FUNC_GETPAIR,
                Arrays.asList(new Address(tokenA), new Address(tokenB)), Arrays.asList(new TypeReference<Address>() {
        }));
        String value = EthCallConstantCall(FACTORY_ADDRESS, FACTORY_ADDRESS, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());

    }


    public  List<Type> allPairs(BigInteger num) {

        final Function function = new Function(
                FUNC_ALLPAIRS,
                Arrays.asList(new Uint(num)),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String value = EthCallConstantCall(FACTORY_ADDRESS, FACTORY_ADDRESS, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public  List<Type> allPairsLength() {

        final Function function = new Function(
                FUNC_ALLPAIRSLENGTH,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint>() {
                }));
        String value = EthCallConstantCall(FACTORY_ADDRESS, FACTORY_ADDRESS, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }



    public  List<Type> token0(String contractAddress) {
        final Function function = new Function(
                FUNC_TOKEN0,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String value = EthCallConstantCall(contractAddress, contractAddress, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }



    public  List<Type> wETH(String contractAddress) {
        final Function function = new Function(
                FUNC_WETH,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String value = EthCallConstantCall(contractAddress, contractAddress, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }

    public  List<Type> symbol(String contractAddress) {
        final Function function = new Function(
                FUNC_SYMBOL,
                Collections.emptyList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        String value = EthCallConstantCall(contractAddress, contractAddress, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public List<Type> token1(String contractAddress) {
        final Function function = new Function(
                FUNC_TOKEN1,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String value = EthCallConstantCall(contractAddress, contractAddress, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public List<Type> getReserves(String contractAddress) {
        final Function function = new Function(
                FUNC_GETRESERVES,
                Collections.emptyList(), Arrays.asList(new TypeReference<Uint112>() {
        }, new TypeReference<Uint112>() {
        }, new TypeReference<Uint32>() {
        }));
        String value = EthCallConstantCall(contractAddress, contractAddress, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public EthSendTransaction approve(Credentials credentials, String contractAddress, BigInteger value) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.asList(new Address(ROUTER_ADDRESS), new Uint(value))
                , Collections.emptyList());

        return submit(credentials, contractAddress, BigInteger.ZERO, FunctionEncoder.encode(function),
                getNonce(credentials.getAddress()), getGasPrice(), GAS_LIMIT_FOR_CONSTANT_CALLS);
    }



    public  List<Type> getAmountsIn(BigInteger amountOut, LinkedList<String> path) {
        final Function function = new Function(
                FUNC_GETAMOUNTIN,
                Arrays.asList(new Uint(amountOut), new DynamicArray(ConvertPath(path))),
                Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
                }));
        String value = EthCallConstantCall(ROUTER_ADDRESS, ROUTER_ADDRESS, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public  List<Type> getAmountsOut(BigInteger amountIn, LinkedList<String> path) {
        final Function function = new Function(
                FUNC_GETAMOUNTOUT,
                Arrays.asList(new Uint(amountIn), new DynamicArray(ConvertPath(path))),
                Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
                }));
        String value = EthCallConstantCall(ROUTER_ADDRESS, ROUTER_ADDRESS, function);
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }


    public EthSendTransaction swapTokensForExactETH(Credentials credentials, BigInteger amountOut, BigInteger amountInMax,
                                             LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPTOKENSFOREXACTETH,
                Arrays.asList(new Uint(amountOut), new Uint(amountInMax), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return  submit(credentials, ROUTER_ADDRESS, BigInteger.ZERO, FunctionEncoder.encode(function),
                nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }


    public  EthSendTransaction swapETHForExactTokens(Credentials credentials, BigInteger amountOut, BigInteger amountInMax,
                                             LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPETHFOREXACTTOKENS,
                Arrays.asList(new Uint(amountOut), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return  submit(credentials, ROUTER_ADDRESS, amountInMax, FunctionEncoder.encode(function),
                nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }

    public  EthSendTransaction swapTokensForExactTokens(Credentials credentials, BigInteger amountOut, BigInteger amountInMax,
                                                        LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPTOKENSFOREXACTTOKENS,
                Arrays.asList(new Uint(amountOut), new Uint(amountInMax), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return submit(credentials, ROUTER_ADDRESS, BigInteger.ZERO, FunctionEncoder.encode(function),
                nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }



    public  EthSendTransaction swapExactTokensForETH(Credentials credentials, BigInteger amountIn, BigInteger amountOutMin,
                                                     LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPEXACTTOKENSFORETH,
                Arrays.asList(new Uint(amountIn), new Uint(amountOutMin), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return  submit(credentials, ROUTER_ADDRESS, BigInteger.ZERO, FunctionEncoder.encode(function),
                nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }


    public  EthSendTransaction swapExactETHForTokens(Credentials credentials, BigInteger amountIn, BigInteger amountOutMin,
                                             LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPEXACTETHFORTOKENS,
                Arrays.asList(new Uint(amountOutMin), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return  submit(credentials, ROUTER_ADDRESS, amountIn, FunctionEncoder.encode(function),
                nonce, gasPrice, GAS_LIMIT_FOR_CONSTANT_CALLS);
    }

    public EthSendTransaction swapExactTokensForTokens(Credentials credentials, BigInteger amountIn, BigInteger amountOutMin,
                                                       LinkedList<String> path, String to, BigInteger deadline, BigInteger nonce, BigInteger gasPrice) {
        final Function function = new Function(
                FUNC_SWAPEXACTTOKENSFORTOKENS,
                Arrays.asList(new Uint(amountIn), new Uint(amountOutMin), new DynamicArray(ConvertPath(path)), new Address(to), new Uint(deadline))
                , Arrays.asList(new TypeReference<DynamicArray<Uint>>() {
        }));
        return  submit(credentials, ROUTER_ADDRESS, BigInteger.ZERO, FunctionEncoder.encode(function),
                nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }


    public EthSendTransaction ethTransfer(Credentials credentials, String toAddress, BigInteger value, BigInteger nonce, BigInteger gasPrice) {
        return  submit(credentials, toAddress, value, "", nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }


    public   EthSendTransaction tokenTransfer(Credentials credentials, String contract, String toAddress, BigInteger value, BigInteger nonce, BigInteger gasPrice) {
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint(value)),
                Arrays.asList(new TypeReference<Type>() {
                }));
        String data = FunctionEncoder.encode(function);
        return  submit(credentials, contract, BigInteger.ZERO, data, nonce, gasPrice,  GAS_LIMIT_FOR_CONSTANT_CALLS);
    }

    public static List<Address> ConvertPath(LinkedList<String> token) {
        return token.stream().map(x -> new Address(x)).collect(Collectors.toList());
    }


}
