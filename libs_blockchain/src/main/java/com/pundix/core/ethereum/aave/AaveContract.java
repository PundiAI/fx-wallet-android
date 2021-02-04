package com.pundix.core.ethereum.aave;

import com.pundix.core.FunctionxNodeConfig;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint40;
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
public class AaveContract extends Contract {

    private static final String BINARY = "Bin file was not provided";
    public static final String LendingPoolAddressesProvider = FunctionxNodeConfig.getInstance().isEthereumMain() ? "0xB53C1a33016B2DC2fF3653530bfF1848a515c8c5" : "0x88757f2f99175387ab4c6a4b3067c77a695b0349";
    public static final String ProtocolDataProvider = FunctionxNodeConfig.getInstance().isEthereumMain() ? "0x057835Ad21a177dbdd3090bB1CAE03EaCF78Fc6d" : "0x3c73A5E5785cAC854D468F727c606C07488a29D6";
    public static final String FUNC_DEPOSIT = "deposit";
    public static final String FUNC_DEPOSIT_ETH = "depositETH";
    public static final String FUNC_GET_LENDING_POOL_CORE = "getLendingPoolCore";
    public static final String FUNC_GET_LENDING_POOL = "getLendingPool";
    public static final String FUNC_GET_RESERVE_CONFIGURATION_DATA = "getReserveConfigurationData";
    public static final String FUNC_GET_RESERVE_DATA = "getReserveData";
    public static final String FUNC_GET_USER_RESERVE_DATA = "getUserReserveData";
    public static final String FUNC_WITHDRAW = "withdraw";
    public static final String FUNC_WITHDRAW_ETH = "withdrawETH";
    public static final String FUNC_GET_ALL_TOKEN = "getAllATokens";
    public static final String FUNC_GET_ALL_RESERVES_TOKEN = "getAllReservesTokens";

    @Deprecated
    protected AaveContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);

    }

    protected AaveContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected AaveContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected AaveContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static String depositToAbi(String asset, BigInteger amount, String onBehalfOf, BigInteger _referralCode) {
        final Function function = new Function(FUNC_DEPOSIT,
                Arrays.<Type>asList(new Address(asset),
                        new Uint256(amount),
                        new Address(onBehalfOf),
                        new Uint16(_referralCode)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String depositEthToAbi(String onBehalfOf, BigInteger _referralCode) {
        final Function function = new Function(FUNC_DEPOSIT_ETH,
                Arrays.<Type>asList(new Address(onBehalfOf),
                        new Uint16(_referralCode)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String withdrawToAbi(String asset, BigInteger _amount, String to) {
        final Function function = new Function(FUNC_WITHDRAW,
                Arrays.<Type>asList(new Address(asset),
                        new Uint256(_amount),
                        new Address(to)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String withdrawEthToAbi(BigInteger _amount, String to) {
        final Function function = new Function(FUNC_WITHDRAW_ETH,
                Arrays.<Type>asList(new Uint256(_amount),
                        new Address(to)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }


    public RemoteCall<List<Type>> getUserReserveData(String _reserve, String _user) {
        final Function function = new Function(FUNC_GET_USER_RESERVE_DATA,
                Arrays.<Type>asList(new Address(_reserve), new Address(_user)),
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
                        new TypeReference<Uint40>() {
                        },
                        new TypeReference<Bool>() {
                        }));
        return executeRemoteCallMultipleValueReturn(function);
    }


    public RemoteCall<List<Type>> getReserveData(String _reserve) {
        final Function function = new Function(FUNC_GET_RESERVE_DATA,
                Arrays.<Type>asList(new Address(_reserve)),
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
                        }, new TypeReference<Uint256>() {
                        },

                        new TypeReference<Uint40>() {
                        }
                ));
        return executeRemoteCallMultipleValueReturn(function);
    }


    public RemoteCall<List<Type>> getReserveConfigurationData(String _reserve) {
        final Function function = new Function(FUNC_GET_RESERVE_CONFIGURATION_DATA,
                Arrays.<Type>asList(new Address(_reserve)),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Bool>() {
                        },
                        new TypeReference<Bool>() {
                        },
                        new TypeReference<Bool>() {
                        },
                        new TypeReference<Bool>() {
                        }));
        return executeRemoteCallMultipleValueReturn(function);
    }


    public RemoteCall<Address> getLendingPoolCore() {
        final Function function = new Function(FUNC_GET_LENDING_POOL_CORE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function);
    }


    public String getAllATokensToAbi() {
        final Function function = new Function(FUNC_GET_ALL_TOKEN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList());
        return FunctionEncoder.encode(function);
    }

    public String getAllReservesTokens() {
        final Function function = new Function(FUNC_GET_ALL_RESERVES_TOKEN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList());
        return FunctionEncoder.encode(function);
    }

    public RemoteCall<Address> getLendingPool() {
        final Function function = new Function(FUNC_GET_LENDING_POOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function);
    }


    @Deprecated
    public static AaveContract load(String contractAddress, Web3j web3j, Credentials
            credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AaveContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static AaveContract load(String contractAddress, Web3j web3j, TransactionManager
            transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AaveContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static AaveContract load(String contractAddress, Web3j web3j, Credentials
            credentials, ContractGasProvider contractGasProvider) {
        return new AaveContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static AaveContract load(String contractAddress, Web3j web3j, TransactionManager
            transactionManager, ContractGasProvider contractGasProvider) {
        return new AaveContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class TokenData extends DynamicStruct {

        public String symbol;
        public String tokenAddress;


        public TokenData(Utf8String symbol, Address tokenAddress) {
            super(symbol, tokenAddress);
            this.symbol = symbol.toString();
            this.tokenAddress = tokenAddress.toString();
        }

    }
}
