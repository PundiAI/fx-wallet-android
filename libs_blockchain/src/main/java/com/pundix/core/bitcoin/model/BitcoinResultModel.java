package com.pundix.core.bitcoin.model;

/**
 * @ClassName: BitcoinResultModel
 * @Description:
 * @Author: Joker
 * @CreateDate: 2020/6/16 11:30 AM
 */
public class BitcoinResultModel {
    private BitcoinTx tx;

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
}
