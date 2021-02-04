package com.pundix.core.ethereum.fxbridge;

import com.pundix.core.ethereum.erc20.ERC20Contract;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

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
public class FxBridgeContract extends ERC20Contract {

    public static final String ManagerBridge = "0x34D640F520A6f94BDE5Ca3f130c510d347A107A1";

    protected FxBridgeContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FxBridgeContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(contractAddress, web3j, credentials, contractGasProvider);
    }

    protected FxBridgeContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FxBridgeContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(contractAddress, web3j, transactionManager, contractGasProvider);
    }


    @Deprecated
    public static FxBridgeContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FxBridgeContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FxBridgeContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FxBridgeContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FxBridgeContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FxBridgeContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FxBridgeContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FxBridgeContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static final String FUNC_LOCK = "lock";
    public static final String FUNC_FX_ACCNOCE = "fxNonce";
    public static final String FUNC_SUBMIT_PROOF = "submitProof";
    public static final String FUNC_ORACLE = "oracle";
    public static final String FUNC_VALIDATOR_ENOUGH = "validatorEnough";
    public static final String FUNC_BRIDGE_RECORDS = "bridgeRecords";
    public static final String FUNC_VALID_BRIDGE_TOKEN = "validBridgeToken";
    public static final String FUNC_GET_BRIDGE_TOKEN_LIST = "getBridgeTokenList";

    public RemoteFunctionCall<BigInteger> fxNonce(String param0) {
        final Function function = new Function(FUNC_FX_ACCNOCE,
                Arrays.<Type>asList(new Utf8String(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }


    public RemoteFunctionCall<String> oracle() {
        final Function function = new Function(FUNC_ORACLE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<List<Type>> validBridgeToken(String ercContractAddress) {
        final Function function = new Function(FUNC_VALID_BRIDGE_TOKEN,
                Arrays.<Type>asList(new Address(ercContractAddress)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeRemoteCallMultipleValueReturn(function);
    }


    public RemoteFunctionCall<List<Type>> getBridgeTokenList() {
        final Function function = new Function(FUNC_GET_BRIDGE_TOKEN_LIST,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<DynamicArray<Address>>() {
                        },
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },
                        new TypeReference<DynamicArray<Uint8>>() {
                        }
                ));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public static String getBridgeTokenListToAbi() {
        final Function function = new Function(FUNC_GET_BRIDGE_TOKEN_LIST,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<DynamicArray<Address>>() {
                        },
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },
                        new TypeReference<DynamicArray<Utf8String>>() {
                        },
                        new TypeReference<DynamicArray<Uint8>>() {
                        }
                ));
        return FunctionEncoder.encode(function);
    }


    public RemoteFunctionCall<List<Type>> validatorEnough(String _messageHash, String sign) {

        final Function function = new Function(FUNC_VALIDATOR_ENOUGH,
                Arrays.<Type>asList(new Bytes32(Numeric.hexStringToByteArray(_messageHash)), new DynamicBytes(Numeric.hexStringToByteArray(sign))),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Bool>() {
                        },
                        new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }));
        return executeRemoteCallMultipleValueReturn(function);
    }


    public RemoteFunctionCall<List<Type>> bridgeRecords(String hrp) {

        final Function function = new Function(FUNC_BRIDGE_RECORDS,
                Arrays.<Type>asList(new Utf8String(hrp)),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Address>() {
                        },
                        new TypeReference<Address>() {
                        }));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public static String lockToAbi(String _recipient, String _token, BigInteger _amount) {
        final Function function = new Function(
                FUNC_LOCK,
                Arrays.<Type>asList(
                        new Utf8String(_recipient),
                        new Address(_token),
                        new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String submitProofToAbi(BigInteger _proofType, String _receiver, String _fxAddress, String _token, BigInteger _amount, byte[] _signature) {
        final Function function = new Function(
                FUNC_SUBMIT_PROOF,
                Arrays.<Type>asList(new Uint8(_proofType),
                        new Address(_receiver),
                        new Utf8String(_fxAddress),
                        new Address(_token),
                        new Uint256(_amount),
                        new DynamicBytes(_signature)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }


}
