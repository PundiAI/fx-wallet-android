package com.pundix.core.bitcoin;

import com.pundix.core.factory.TransationData;

public class BitcoinTransationData extends TransationData {

    private static final long serialVersionUID = 1906792461389715166L;
    private String fromAddress;
    private String toAddress;
    private String privateKey;
    private String amount;
    private String fee;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
