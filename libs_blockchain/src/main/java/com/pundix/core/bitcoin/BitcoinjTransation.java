package com.pundix.core.bitcoin;

import com.pundix.core.bitcoin.model.BitcoinAccountModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;
import com.pundix.core.bitcoin.model.BitcoinUtxoModel;
import com.pundix.core.bitcoin.service.BitcoinHttp;
import com.pundix.core.factory.TransationResult;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * @ClassName: BitcoinRpcService
 * @Author: Joker
 * @CreateDate: 2019-11-06 16:24
 */
public class BitcoinjTransation implements ITransation {
    private NetworkParameters networkParameters = MainNetParams.get();
    private long feeRate = 20;

    @Override
    public TransationResult sendTransation(TransationData data) throws IOException {
        TransationResult resp = new TransationResult();
        String fromAddress = data.getFromAddress();
        Response<BitcoinAccountModel> bitcoinResponse = BitcoinHttp.getAccountUtxo(fromAddress).execute();
        List<BitcoinUtxoModel> utxoModelList = bitcoinResponse.body().getUnspent_outputs();
        final List<UTXO> utxoList = formatUtxo(utxoModelList);
        long fee = getFee(Long.valueOf(data.getValue()),utxoList);
        String sign = sign(fee,data,utxoList);
        Response<BitcoinResultModel> resultResponse = BitcoinHttp.sendTransaction(sign).execute();
        resp.setHash(resultResponse.body().getTx().getHash());
        return resp;
    }

    @Override
    public String getBalance(String address) {
        return null;
    }

    private String sign(long fee,TransationData data, List<UTXO> utxos) {
        Transaction transaction = new Transaction(networkParameters);
        long amount = Long.valueOf(data.getValue());
        String toAddress = data.getToAddress();
        String changeAddress = data.getFromAddress();
        long changeAmount = 0L;
        long utxoAmount = 0L;
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
            throw new RuntimeException("utxo balance not enough");
        }
        if (changeAmount > 0) {
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, changeAddress));
        }
        ECKey ecKey = ECKey.fromPrivate(new BigInteger(data.getPrivateKey(), 16));
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
                Script redeemScript = segWitRedeemScript(ecKey);
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

    private List<UTXO> formatUtxo(List<BitcoinUtxoModel> utxos) {
        List<UTXO> utxoList = new ArrayList<>();
        if (utxos == null || utxos.size() == 0) {
            throw new RuntimeException("utxo is null");
        }
        for (BitcoinUtxoModel bitcoinUtxoModel : utxos) {
            UTXO utxo = new UTXO(Sha256Hash.wrap(bitcoinUtxoModel.getTx_hash_big_endian()),
                    bitcoinUtxoModel.getTx_output_n(),
                    Coin.valueOf(bitcoinUtxoModel.getValue()),
                    0, false,
                    new Script(Hex.decode(bitcoinUtxoModel.getScript())));
            utxoList.add(utxo);
            return utxoList;
        }
        return utxoList;
    }


    public Script segWitRedeemScript(ECKey ecKey) {
        byte[] hash = Utils.sha256hash160(ecKey.getPubKey());
        byte[] buf = new byte[2 + hash.length];
        buf[0] = 0x00; // OP_0
        buf[1] = 0x14;// push 20 bytes
        System.arraycopy(hash, 0, buf, 2, hash.length); // keyhash
        return new Script(buf);
    }

    public Script segWitOutputScript(ECKey ecKey) {
        byte[] hash = Utils.sha256hash160(segWitRedeemScript(ecKey).getProgram());
        byte[] buf = new byte[3 + hash.length];
        buf[0] = (byte) 0xa9;// HASH160
        buf[1] = 0x14; // push 20 bytes
        System.arraycopy(hash, 0, buf, 2, hash.length); // keyhash
        buf[22] = (byte) 0x87; // OP_EQUAL
        return new Script(buf);
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
                fee = (utxoSize * 148 + 34 * 3 + 10) * feeRate;
            }
        }
        return fee;
    }
}
