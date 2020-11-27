package com.pundix.core.bitcoin.model;

/**
 * @ClassName: BitcoinUtxoModel
 * @Author: Joker
 * @CreateDate: 2019-11-07 17:23
 */
public class BitcoinUtxoModel {
    private String tx_hash;
    private String tx_hash_big_endian;
    private long tx_output_n;
    private String script;
    private long value;
    private String value_hex;
    private long confirmations;
    private long tx_index;

    public String getTx_hash() {
        return tx_hash;
    }

    public void setTx_hash(String tx_hash) {
        this.tx_hash = tx_hash;
    }

    public String getTx_hash_big_endian() {
        return tx_hash_big_endian;
    }

    public void setTx_hash_big_endian(String tx_hash_big_endian) {
        this.tx_hash_big_endian = tx_hash_big_endian;
    }

    public long getTx_output_n() {
        return tx_output_n;
    }

    public void setTx_output_n(long tx_output_n) {
        this.tx_output_n = tx_output_n;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getValue_hex() {
        return value_hex;
    }

    public void setValue_hex(String value_hex) {
        this.value_hex = value_hex;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public long getTx_index() {
        return tx_index;
    }

    public void setTx_index(long tx_index) {
        this.tx_index = tx_index;
    }
}
