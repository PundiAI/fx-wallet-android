package com.pundix.core.bitcoin.model;

/**
 * @ClassName: BitcoinResultModel
 * @Description:
 * @Author: YT
 * @CreateDate: 2020/6/16 11:30 AM
 */
public class BitcoinResultModel {
    private BitcoinTx tx;
    private String error;

    public BitcoinTx getTx() {
        return tx;
    }

    public void setTx(BitcoinTx tx) {
        this.tx = tx;
    }

    public class BitcoinTx{
        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        private String hash;

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
