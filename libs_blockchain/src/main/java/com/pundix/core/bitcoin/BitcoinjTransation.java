package com.pundix.core.bitcoin;

import android.text.TextUtils;
import android.util.Log;

import com.pundix.common.constants.BitcoinUtil;
import com.pundix.common.utils.GsonUtils;
import com.pundix.core.bitcoin.model.BitcoinBalanceModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;
import com.pundix.core.bitcoin.model.BitcoinTxModel;
import com.pundix.core.bitcoin.service.BitcoinHttp;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BitcoinjTransation implements ITransation {

    @Override
    public TransationResult sendTransation(TransationData transationData) {
        TransationResult resp = new TransationResult();
        BitcoinTransationData bitcoinTransationData = (BitcoinTransationData) transationData;
        BitcoinResultModel bitcoinResultModel = null;
        try {
            final BitcoinBalanceModel bitcoinBalanceModel = BitcoinHttp.getAccountUtxo(bitcoinTransationData.getFromAddress()).execute().body();
            final List<UTXO> utxos = convterToUtxo(bitcoinBalanceModel);
            final String tx = btcSign(bitcoinTransationData, utxos);
            bitcoinResultModel = BitcoinHttp.sendTransaction(tx).execute().body();
            resp.setCode(0);
            resp.setHash(bitcoinResultModel.getTx().getHash());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg(e.getMessage());
        }
        return resp;
    }


    private List<UTXO> convterToUtxo(BitcoinBalanceModel bitcoinBalanceModel) {
        final List<BitcoinTxModel> txs = bitcoinBalanceModel.getTxrefs();
        if (txs == null) {
            throw new RuntimeException("Insufficient Balance");
        }
        List<UTXO> data = new ArrayList<>();
        for (BitcoinTxModel bitcoinTxModel : txs) {
            UTXO utxo = new UTXO(Sha256Hash.wrap(bitcoinTxModel.getTx_hash()),
                    bitcoinTxModel.getTx_output_n(),
                    Coin.valueOf(bitcoinTxModel.getValue()),
                    0, false,
                    new Script(Hex.decode(bitcoinTxModel.getScript())));
            data.add(utxo);
        }
        return data;
    }

    @Override
    public String getBalance(String... address) {
        return getArrayBalance(address).get(0);
    }

    @Override
    public List<String> getArrayBalance(String... address) {
        List<String> amoutArray = new ArrayList<>();
        if (address.length > 1) {
            try {
                final List<BitcoinBalanceModel> body = BitcoinHttp.getBalanceArray(TextUtils.join(";", address)).execute().body();
                for (String ads : address) {
                    for (BitcoinBalanceModel bitcoinBalanceModel : body) {
                        if (ads.equals(bitcoinBalanceModel.getAddress())) {
                            amoutArray.add(bitcoinBalanceModel.getFinal_balance());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                for (String tempAddress : address) {
                    amoutArray.add("");
                }
            }
        } else {
            try {
                BitcoinBalanceModel bitcoinBalanceModel = BitcoinHttp.getBalance(address[0]).execute().body();
                amoutArray.add(bitcoinBalanceModel.getFinal_balance());
            } catch (Exception e) {
                e.printStackTrace();
                amoutArray.add("");
            }
        }
        return amoutArray;
    }

    @Override
    public Object getFee(TransationData data) throws IOException {
        BitcoinTransationData bitcoinTransationData = (BitcoinTransationData) data;
        final BitcoinBalanceModel bitcoinBalanceModel = BitcoinHttp.getAccountUtxo((bitcoinTransationData).getFromAddress()).execute().body();
        final List<UTXO> utxos = convterToUtxo(bitcoinBalanceModel);
        return getFee(Long.parseLong(bitcoinTransationData.getAmount()), utxos);
    }

    @Override
    public Object getTxs(Object hash) {
        try {
            return BitcoinHttp.getTxs(hash.toString()).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getFee(long amount, List<UTXO> utxos) {
        Long utxoAmount = 0L;
        Long fee = 0L;
        Long utxoSize = 0L;
        for (UTXO us : utxos) {
            utxoSize++;
            if (utxoAmount >= (amount + fee)) {
                break;
            } else {
                utxoAmount += us.getValue().value;
                fee = (utxoSize * 148 + 34 * 3 + 10);
            }
        }
        return fee;
    }


    private String btcSign(BitcoinTransationData bitcoinTransationData, List<UTXO> utxos) {
        NetworkParameters networkParameters = bitcoinTransationData.getNetworkParameters();
        Long amount = Long.valueOf(bitcoinTransationData.getAmount());
        Long fee = Long.valueOf(bitcoinTransationData.getFee());
        String toAddress = bitcoinTransationData.getToAddress();
        String changeAddress = bitcoinTransationData.getFromAddress();
        Transaction transaction = new Transaction(networkParameters);
        Long changeAmount = 0L;
        Long utxoAmount = 0L;
        List<UTXO> needUtxos = new ArrayList<>();
        for (UTXO utxo : utxos) {
            if (utxoAmount >= (amount + fee)) {
                break;
            } else {
                needUtxos.add(utxo);
                utxoAmount += utxo.getValue().value;
            }
        }
        transaction.addOutput(Coin.valueOf(amount), Address.fromString(networkParameters, toAddress));
        changeAmount = utxoAmount - (amount + fee);
        if (changeAmount < 0) {
            throw new RuntimeException("Insufficient Balance");
        }
        if (changeAmount > 0) {
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, changeAddress));
        }
        ECKey ecKey1 = ECKey.fromPrivate(new BigInteger(bitcoinTransationData.getPrivateKey(), 16));
        String privateKeyAsWiF = ecKey1.getPrivateKeyAsWiF(networkParameters);
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, privateKeyAsWiF);
        ECKey ecKey = dumpedPrivateKey.getKey();
        for (int i = 0; i < needUtxos.size(); i++) {
            UTXO utxo = needUtxos.get(i);
            TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, utxo.getIndex(), utxo.getHash());
            TransactionInput input = new TransactionInput(networkParameters, transaction, new byte[0], outPoint, utxo.getValue());
            transaction.addInput(input);
            Script script = utxo.getScript();
            if (ScriptPattern.isP2SH(script)) {
                byte[] program = ScriptBuilder.createOutputScript(LegacyAddress.fromKey(networkParameters, ecKey)).getProgram();
                Script scriptCode = new ScriptBuilder().data(program).build();
                TransactionSignature signature = transaction.calculateWitnessSignature(i, ecKey, scriptCode, utxo.getValue(), Transaction.SigHash.ALL, true);
                ScriptBuilder sigScript = new ScriptBuilder();
                Script redeemScript = BitcoinUtil.segWitRedeemScript(ecKey);
                sigScript.data(redeemScript.getProgram());
                input.setScriptSig(sigScript.build());
                input.setWitness(TransactionWitness.redeemP2WPKH(signature, ecKey));
            }
            else if (ScriptPattern.isP2PKH(script)) {
                TransactionSignature signature = transaction.calculateSignature(i, ecKey, script, Transaction.SigHash.ALL, true);
                input.setScriptSig(ScriptBuilder.createInputScript(signature, ecKey));
                input.setWitness(null);
            } else if (ScriptPattern.isP2WPKH(script)) {
                Script scriptCode = new ScriptBuilder()
                        .data(ScriptBuilder.createOutputScript(LegacyAddress.fromKey(networkParameters, ecKey)).getProgram()).build();
                TransactionSignature signature = transaction.calculateWitnessSignature(i, ecKey, scriptCode, utxo.getValue(), Transaction.SigHash.ALL, true);
                input.setScriptSig(ScriptBuilder.createEmpty());
                input.setWitness(TransactionWitness.redeemP2WPKH(signature, ecKey));
            }
        }

        byte[] bytes = transaction.bitcoinSerialize();
        String hash = Hex.toHexString(bytes);
        return hash;
    }


}
