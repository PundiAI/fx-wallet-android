package com.pundix.core.ethereum.yfi;

import com.pundix.core.FunctionxNodeConfig;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.1.1.
 */
public class YearnContract extends Contract {
    private static final String BINARY = "Bin file was not provided";


    public static final String IEarnAPR = FunctionxNodeConfig.getInstance().isEthereumMain() ? "0x9CaD8AB10daA9AF1a9D2B878541f41b697268eEC" : "";

    public static final String FUNC_GET_DAI = "getDAI";
    public static final String FUNC_DEPOSIT = "deposit";
    public static final String FUNC_WITHDRAW = "withdraw";
    public static final String FUNC_GET_PRICE_PER_FULL_SHARE = "getPricePerFullShare";
    @Deprecated
    protected YearnContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);

    }

    protected YearnContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);

    }

    @Deprecated
    protected YearnContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);

    }

    protected YearnContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<List<Type>> getPricePerFullShare() {
        final Function function = new Function(FUNC_GET_PRICE_PER_FULL_SHARE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));


        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<List<Type>> getDAI() {
        final Function function = new Function(FUNC_GET_DAI,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }
                ));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public static String withdrawToAbi(BigInteger _amount) {
        final Function function = new Function(FUNC_WITHDRAW,
                Arrays.<Type>asList(new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String depositToAbi(BigInteger _amount) {
        final Function function = new Function(FUNC_DEPOSIT,
                Arrays.<Type>asList(new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }


    @Deprecated
    public static YearnContract load(String contractAddress, Web3j web3j, Credentials
            credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new YearnContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static YearnContract load(String contractAddress, Web3j web3j, TransactionManager
            transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new YearnContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static YearnContract load(String contractAddress, Web3j web3j, Credentials
            credentials, ContractGasProvider contractGasProvider) {
        return new YearnContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static YearnContract load(String contractAddress, Web3j web3j, TransactionManager
            transactionManager, ContractGasProvider contractGasProvider) {
        return new YearnContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }


}
