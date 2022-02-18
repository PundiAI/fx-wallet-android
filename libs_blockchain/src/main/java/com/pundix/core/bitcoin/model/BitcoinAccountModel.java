package com.pundix.core.bitcoin.model;

import java.util.List;

/**
 * @ClassName: BitcoinAccountModel
 * @Description:
 * @Author: YT
 * @CreateDate: 2020/6/15 2:14 PM
 */
public class BitcoinAccountModel {
   private String  notice;
    private List<BitcoinUtxoModel> unspent_outputs;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public List<BitcoinUtxoModel> getUnspent_outputs() {
        return unspent_outputs;
    }

    public void setUnspent_outputs(List<BitcoinUtxoModel> unspent_outputs) {
        this.unspent_outputs = unspent_outputs;
    }
}
