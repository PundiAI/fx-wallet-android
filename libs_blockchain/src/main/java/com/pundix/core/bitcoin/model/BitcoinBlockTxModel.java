package com.pundix.core.bitcoin.model;

/**
 * @ClassName: BitcoinBlockTxModel
 * @Description:
 * @Author: YT
 * @Date: 3/23/21
 */
public class BitcoinBlockTxModel {
    String block_hash;
    String block_height;
    String hash;
    String confirmations;

    public String getBlock_hash() {
        return block_hash;
    }

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public String getBlock_height() {
        return block_height;
    }

    public void setBlock_height(String block_height) {
        this.block_height = block_height;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }
}
