package com.pundix.core.ethereum.sushi;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: SushiContract
 * @Description:
 * @Author: Joker
 * @CreateDate: 2020/12/2
 */
public class SushiContract extends Contract {
    private static final String BINARY = "Bin file was not provided";
    private static final String WETH_ADDRESS = "WETH";
    public static final String T0TAL_AMOUNT = "totalAmount";

    protected SushiContract(String contractBinary, String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, transactionManager, gasProvider);
    }

    protected SushiContract(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, credentials, gasProvider);
    }


    public static SushiContract load(String contractAddress, Web3j web3j, Credentials
            credentials, ContractGasProvider contractGasProvider) {
        return new SushiContract(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SushiContract load(String contractAddress, Web3j web3j, TransactionManager
            transactionManager, ContractGasProvider contractGasProvider) {
        return new SushiContract(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<List<Type>> WETH() {
        final Function function = new Function(WETH_ADDRESS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<List<Type>> totalAmount(String address){
        final Function function = new Function(T0TAL_AMOUNT,
                Arrays.<Type>asList(new Address(address)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallMultipleValueReturn(function);

    }

}
