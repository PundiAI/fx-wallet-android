package com.pundix.core.model;

public class UnLockModel {

    String token_contract;
    String claim_hash;
    String eth_receiver;
    String fx_sender;
    AmountModel amount;
    String sign;
    int decimal;

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public String getClaim_hash() {
        return claim_hash;
    }

    public void setClaim_hash(String claim_hash) {
        this.claim_hash = claim_hash;
    }

    public String getToken_contract() {
        return token_contract;
    }

    public void setToken_contract(String token_contract) {
        this.token_contract = token_contract;
    }

    public String getEth_receiver() {
        return eth_receiver;
    }

    public void setEth_receiver(String eth_receiver) {
        this.eth_receiver = eth_receiver;
    }

    public String getFx_sender() {
        return fx_sender;
    }

    public void setFx_sender(String fx_sender) {
        this.fx_sender = fx_sender;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
