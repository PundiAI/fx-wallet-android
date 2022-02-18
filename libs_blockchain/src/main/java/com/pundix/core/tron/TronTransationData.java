package com.pundix.core.tron;

import com.pundix.core.factory.TransationData;
import com.pundix.core.model.TronFee;

import org.tron.protos.contract.Common;

public class TronTransationData extends TransationData {

    private static final long serialVersionUID = -4138198585534621579L;

    String from;
    String to;
    String amount;
    String privateKey;
    String contractAddress;
    String data;
    String feeLimit;


    public String getFeeLimit() {
        return feeLimit;
    }

    public void setFeeLimit(String feeLimit) {
        this.feeLimit = feeLimit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    TronTranserType TronTranserType;
    Common.ResourceCode resourceCode;
    private TronFee tronFee;

    public TronFee getTronFee() {
        return tronFee;
    }

    public void setTronFee(TronFee tronFee) {
        this.tronFee = tronFee;
    }

    public String getContractAddress() {
        return contractAddress;
    }


    public TronTranserType getTronTranserType() {
        return TronTranserType;
    }

    public void setTronTranserType(com.pundix.core.tron.TronTranserType tronTranserType) {
        TronTranserType = tronTranserType;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getFrom() {
        return from;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public Common.ResourceCode getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(Common.ResourceCode resourceCode) {
        this.resourceCode = resourceCode;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
