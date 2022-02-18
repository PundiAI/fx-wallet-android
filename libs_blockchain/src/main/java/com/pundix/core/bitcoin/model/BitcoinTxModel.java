package com.pundix.core.bitcoin.model;

/**
 * @ClassName: BitcoinTxModel
 * @Author: YT
 * @CreateDate: 2020/12/1 1:44 PM
 */
public class BitcoinTxModel {

    String tx_hash;
    long block_height;
    int tx_input_n;
    int tx_output_n;
    long value;
    long ref_balance;
    boolean spent;
    long confirmations;
    String confirmed;
    boolean double_spend;
    String script;

    public String getTx_hash() {
        return tx_hash;
    }

    public void setTx_hash(String tx_hash) {
        this.tx_hash = tx_hash;
    }

    public long getBlock_height() {
        return block_height;
    }

    public void setBlock_height(long block_height) {
        this.block_height = block_height;
    }

    public int getTx_input_n() {
        return tx_input_n;
    }

    public void setTx_input_n(int tx_input_n) {
        this.tx_input_n = tx_input_n;
    }

    public int getTx_output_n() {
        return tx_output_n;
    }

    public void setTx_output_n(int tx_output_n) {
        this.tx_output_n = tx_output_n;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getRef_balance() {
        return ref_balance;
    }

    public void setRef_balance(long ref_balance) {
        this.ref_balance = ref_balance;
    }

    public boolean isSpent() {
        return spent;
    }

    public void setSpent(boolean spent) {
        this.spent = spent;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isDouble_spend() {
        return double_spend;
    }

    public void setDouble_spend(boolean double_spend) {
        this.double_spend = double_spend;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
