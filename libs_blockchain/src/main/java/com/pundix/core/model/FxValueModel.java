package com.pundix.core.model;

import java.util.List;

public  class FxValueModel {

    private String address;
    private int account_number;
    private int sequence;
    private List<AmountModel> coins;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAccount_number() {
        return account_number;
    }

    public void setAccount_number(int account_number) {
        this.account_number = account_number;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<AmountModel> getCoins() {
        return coins;
    }

    public void setCoins(List<AmountModel> coins) {
        this.coins = coins;
    }

    @Override
    public String toString() {
        return "FxValueModel{" +
                "address='" + address + '\'' +
                ", account_number=" + account_number +
                ", sequence=" + sequence +
                ", coins=" + coins +
                '}';
    }
}