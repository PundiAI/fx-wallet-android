package com.pundix.core.bitcoin.model;

import java.util.List;

/**
 * @ClassName: BitcoinBalanceModel
 * @Description:
 * @Author: YT
 * @CreateDate: 2020/12/1 11:05 AM
 */
public class BitcoinBalanceModel {

    String address;
    String total_received;
    String total_sent;
    String balance;
    String unconfirmed_balance;
    String final_balance;
    String n_tx;
    String unconfirmed_n_tx;
    String final_n_tx;
    List<BitcoinTxModel> txrefs;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal_received() {
        return total_received;
    }

    public void setTotal_received(String total_received) {
        this.total_received = total_received;
    }

    public String getTotal_sent() {
        return total_sent;
    }

    public void setTotal_sent(String total_sent) {
        this.total_sent = total_sent;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUnconfirmed_balance() {
        return unconfirmed_balance;
    }

    public void setUnconfirmed_balance(String unconfirmed_balance) {
        this.unconfirmed_balance = unconfirmed_balance;
    }

    public List<BitcoinTxModel> getTxrefs() {
        return txrefs;
    }

    public void setTxrefs(List<BitcoinTxModel> txrefs) {
        this.txrefs = txrefs;
    }

    public String getFinal_balance() {
        return final_balance;
    }

    public void setFinal_balance(String final_balance) {
        this.final_balance = final_balance;
    }

    public String getN_tx() {
        return n_tx;
    }

    public void setN_tx(String n_tx) {
        this.n_tx = n_tx;
    }

    public String getUnconfirmed_n_tx() {
        return unconfirmed_n_tx;
    }

    public void setUnconfirmed_n_tx(String unconfirmed_n_tx) {
        this.unconfirmed_n_tx = unconfirmed_n_tx;
    }

    public String getFinal_n_tx() {
        return final_n_tx;
    }

    public void setFinal_n_tx(String final_n_tx) {
        this.final_n_tx = final_n_tx;
    }
}
