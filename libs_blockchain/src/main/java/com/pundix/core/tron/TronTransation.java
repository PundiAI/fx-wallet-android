package com.pundix.core.tron;

import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.Any2;
import com.google.protobuf.ByteString;
import com.pundix.common.utils.ByteUtils;
import com.pundix.common.utils.StringUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.EthereumService;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.model.TransactionTron;
import com.pundix.core.model.TronFee;

import org.bouncycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Sm3Hash;
import org.tron.common.utils.AddressHelper;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.contract.AssetIssueContractOuterClass;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.SmartContractOuterClass;

import java.math.BigInteger;
import java.util.List;

public class TronTransation implements ITransation {

    @Override
    public TransationResult sendTransation(TransationData transationData) throws Exception {
        TronTransationData tronTransation = (TronTransationData) transationData;
        String transactionHash = null;
        final Transaction signTransaction = getSignTransfer(tronTransation);
        final TransationResult transationResult = TronService.getInstance().transfer(signTransaction);
        transactionHash = getTransactionHash(signTransaction);
        transationResult.setHash(transactionHash);
        return transationResult;
    }

    private Transaction getSignTransfer(TronTransationData tronTransation) {
        final TronTranserType type = tronTransation.getTronTranserType();
        Transaction transaction = null;
        ECKey ecKey = ECKey.fromPrivate(new BigInteger(tronTransation.getPrivateKey(), 16));
        if (type == TronTranserType.TRANSFER) {
            transaction = creatTransaction(tronTransation);
        } else if (type == TronTranserType.TRANSFER_TRC10) {
            transaction = creatTrc10Transaction(tronTransation);
        } else if (type == TronTranserType.TRANSFER_CONTRACT) {
            transaction = creatTrc20Transaction(tronTransation);
        } else if (type == TronTranserType.TRANSFER_FREEZE) {
            transaction = creatFreezeTransaction(tronTransation);
        }
        Transaction signTransaction = TransactionUtils.sign(transaction, ecKey);
        return signTransaction;
    }


    private Transaction creatTransaction(TronTransationData tronTransation) {
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        Protocol.Block newestBlock = TronService.getInstance().getBlock(-1);
        Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
        BalanceContract.TransferContract.Builder transferContractBuilder = BalanceContract.TransferContract.newBuilder();
        transferContractBuilder.setAmount(Long.valueOf(tronTransation.getAmount()));
        ByteString bsTo = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getTo()));
        ByteString bsOwner = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getFrom()));
        transferContractBuilder.setToAddress(bsTo);
        transferContractBuilder.setOwnerAddress(bsOwner);
        Any2 any = Any2.pack(transferContractBuilder.build());
        contractBuilder.setParameter(any);
        contractBuilder.setType(Transaction.Contract.ContractType.TransferContract);
        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                .setTimestamp(System.currentTimeMillis())
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
        Transaction transaction = transactionBuilder.build();
        Transaction refTransaction = setReference(transaction, newestBlock);
        return refTransaction;
    }

    private Transaction creatFreezeTransaction(TronTransationData tronTransation) {
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        Protocol.Block newestBlock = TronService.getInstance().getBlock(-1);
        Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
        BalanceContract.FreezeBalanceContract.Builder freezeBalanceContract = BalanceContract.FreezeBalanceContract.newBuilder();
        ByteString bsOwner = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getFrom()));
        freezeBalanceContract.setOwnerAddress(bsOwner);
        freezeBalanceContract.setFrozenBalance(Long.parseLong(tronTransation.getAmount()));
        freezeBalanceContract.setResource(tronTransation.getResourceCode());
        freezeBalanceContract.setFrozenDuration(3);
        if (!tronTransation.getFrom().equals(tronTransation.getTo())) {
            ByteString bsTo = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getTo()));
            freezeBalanceContract.setReceiverAddress(bsTo);
        }
        Any2 any = Any2.pack(freezeBalanceContract.build());
        contractBuilder.setParameter(any);
        contractBuilder.setType(Transaction.Contract.ContractType.FreezeBalanceContract);
        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                .setTimestamp(System.currentTimeMillis())
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
        Transaction transaction = transactionBuilder.build();
        Transaction refTransaction = setReference(transaction, newestBlock);
        return refTransaction;
    }

    private Transaction creatTrc10Transaction(TronTransationData tronTransation) {
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        Protocol.Block newestBlock = TronService.getInstance().getBlock(-1);
        Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
        AssetIssueContractOuterClass.TransferAssetContract.Builder transferContractBuilder = AssetIssueContractOuterClass.TransferAssetContract.newBuilder();
        transferContractBuilder.setAmount(Long.valueOf(tronTransation.getAmount()));
        ByteString bsTo = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getTo()));
        ByteString bsOwner = ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getFrom()));
        transferContractBuilder.setToAddress(bsTo);
        transferContractBuilder.setOwnerAddress(bsOwner);
        transferContractBuilder.setAssetName(ByteString.copyFrom(tronTransation.getContractAddress().getBytes()));
        Any2 any = Any2.pack(transferContractBuilder.build());
        contractBuilder.setParameter(any);
        contractBuilder.setType(Transaction.Contract.ContractType.TransferAssetContract);
        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                .setTimestamp(System.currentTimeMillis())
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
        Transaction transaction = transactionBuilder.build();
        Transaction refTransaction = setReference(transaction, newestBlock);
        return refTransaction;
    }


    private Transaction creatTrc20Transaction(TronTransationData tronTransation) {
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        Protocol.Block newestBlock = TronService.getInstance().getBlock(-1);
        Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
        SmartContractOuterClass.TriggerSmartContract.Builder transferContractBuilder = SmartContractOuterClass.TriggerSmartContract.newBuilder();
        transferContractBuilder.setOwnerAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getFrom())));
        transferContractBuilder.setContractAddress(ByteString.copyFrom(AddressHelper.decodeFromBase58Check(tronTransation.getTo())));
        transferContractBuilder.setCallValue(0L);
        byte[] input = Hex.decode(tronTransation.getData());
        transferContractBuilder.setData(ByteString.copyFrom(input));

        Any2 any = Any2.pack(transferContractBuilder.build());
        contractBuilder.setParameter(any);
        contractBuilder.setType(Transaction.Contract.ContractType.TriggerSmartContract);
        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                .setTimestamp(System.currentTimeMillis())
                .setFeeLimit(Long.parseLong(tronTransation.getFeeLimit()))
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
        Transaction transaction = transactionBuilder.build();
        Transaction refTransaction = setReference(transaction, newestBlock);
        return refTransaction;
    }

    public static String getTransactionHash(Transaction transaction) {
        String txid = ByteArray.toHexString(Sha256Sm3Hash.hash(transaction.getRawData().toByteArray()));
        return txid;
    }

    public static Transaction setReference(Transaction transaction, Protocol.Block newestBlock) {
        long blockHeight = newestBlock.getBlockHeader().getRawData().getNumber();
        byte[] blockHash = getBlockHash(newestBlock).getBytes();
        byte[] refBlockNum = ByteArray.fromLong(blockHeight);
        Transaction.raw rawData = transaction.getRawData().toBuilder()
                .setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)))
                .setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)))
                .build();
        return transaction.toBuilder().setRawData(rawData).build();
    }

    public static Sha256Sm3Hash getBlockHash(Protocol.Block block) {
        return Sha256Sm3Hash.of(block.getBlockHeader().getRawData().toByteArray());
    }

    @Override
    public String getBalance(String... address) {
        String balance = null;
        String contract = address[1];
        final TronTranserType type = getBalanceType(contract);
        try {
            if (type == TronTranserType.TRANSFER) {
                final Protocol.Account account = TronService.getInstance().getBanlance(address[0]);
                balance = String.valueOf(account.getBalance());
            } else if (type == TronTranserType.TRANSFER_TRC10) {
                balance = TronService.getInstance().getTrc10Banlance(address[0], address[1]);
            } else {
                final GrpcAPI.TransactionExtention balanceGrpc = TronService.getInstance().getTrc20Balance(address[0], address[1]);
                if (balanceGrpc != null && balanceGrpc.getResult().getResult()) {
                    balance = new BigInteger(ByteUtils.toHexString(balanceGrpc.getConstantResult(0).toByteArray()), 16).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    private TronTranserType getBalanceType(String contract) {
        if (TextUtils.isEmpty(contract)) {
            return TronTranserType.TRANSFER;
        } else if (StringUtils.isNumeric(contract)) {
            return TronTranserType.TRANSFER_TRC10;
        } else {
            return TronTranserType.TRANSFER_CONTRACT;
        }
    }

    @Override
    public List<String> getArrayBalance(String... address) {
        throw new RuntimeException("Not Support");
    }


    @Override
    public Object getFee(TransationData transationData) throws Exception {
        TronTransationData tronTransation = (TronTransationData) transationData;
        final GrpcAPI.AccountResourceMessage accountResourceMessage = TronService.getInstance().getAccountResourceMessage(tronTransation.getFrom());
        TronFee tronFee = new TronFee();
        if (tronTransation.getTronTranserType() == TronTranserType.TRANSFER_CONTRACT) {
            final long energyUsed = TronService.getInstance().getEnergyUsed(tronTransation);
            long freeEnergy = accountResourceMessage.getEnergyLimit() - accountResourceMessage.getEnergyUsed();
            if (energyUsed >= freeEnergy) {
                tronFee.setEnergy(energyUsed);
                final BigInteger gasPrice = EthereumService.getInstance(Coin.TRON).getGasPrice();
                tronFee.setGasPrice(Long.parseLong(gasPrice.toString()));
            }
        } else {
            final Protocol.Account toAccount = TronService.getInstance().getBanlance(tronTransation.getTo());
            if (toAccount.getCreateTime() == 0) {
                tronFee.setFee(1000000);
                tronFee.setNewAccount(true);
            }
            final Transaction signTransfer = getSignTransfer(tronTransation);
            long serializedSize = signTransfer.toBuilder().clearRet().build().getSerializedSize() + 64;
            long freeBandWith = accountResourceMessage.getFreeNetLimit() - accountResourceMessage.getFreeNetUsed();
            if (serializedSize >= freeBandWith) {
                tronFee.setBandwidth(serializedSize);
            }
        }
        return tronFee;
    }

    @Override
    public Object getTxs(Object hash) {
        TransactionTron transactiontron = new TransactionTron();
        try {
            final Protocol.Transaction transaction = TronService.getInstance().getTransactionById(hash.toString());
            final Protocol.TransactionInfo transactionInfo = TronService.getInstance().getTransactionInfoById(hash.toString());
            transactiontron.setStatus(transaction.getRet(0).getContractRet().getNumber());
            transactiontron.setFee(transactionInfo.getFee() + "");
        } catch (Exception e) {
            e.printStackTrace();
            transactiontron.setStatus(0x2021);
        }
        return transactiontron;
    }


}
