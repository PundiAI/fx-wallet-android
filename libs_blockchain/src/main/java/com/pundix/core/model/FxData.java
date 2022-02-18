package com.pundix.core.model;


import com.pundix.core.enums.MsgType;

import java.io.Serializable;

public class FxData implements Serializable {

    private static final long serialVersionUID = -3623490547175028305L;

    MsgType msgType;
    String fxSender;
    String fxNonce;
    AmountModel amount;
    AmountModel bridgeFee;
    String ethereumChainId;
    String tokenContractAddress;
    String ethereumReceiver;
    String fxBridgeContract;
    String delegatorAddress;
    String validatorAddress;
    String router;

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public AmountModel getBridgeFee() {
        return bridgeFee;
    }

    public void setBridgeFee(AmountModel bridgeFee) {
        this.bridgeFee = bridgeFee;
    }

    public String getDelegatorAddress() {
        return delegatorAddress;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegatorAddress = delegatorAddress;
    }

    public String getValidatorAddress() {
        return validatorAddress;
    }

    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
    }

    public String getFxBridgeContract() {
        return fxBridgeContract;
    }

    public void setFxBridgeContract(String fxBridgeContract) {
        this.fxBridgeContract = fxBridgeContract;
    }

    public MsgType getMsgType() {

        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getFxSender() {
        return fxSender;
    }

    public void setFxSender(String fxSender) {
        this.fxSender = fxSender;
    }

    public String getFxNonce() {
        return fxNonce;
    }

    public void setFxNonce(String fxNonce) {
        this.fxNonce = fxNonce;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public String getEthereumChainId() {
        return ethereumChainId;
    }

    public void setEthereumChainId(String ethereumChainId) {
        this.ethereumChainId = ethereumChainId;
    }

    public String getTokenContractAddress() {
        return tokenContractAddress;
    }

    public void setTokenContractAddress(String tokenContractAddress) {
        this.tokenContractAddress = tokenContractAddress;
    }

    public String getEthereumReceiver() {
        return ethereumReceiver;
    }

    public void setEthereumReceiver(String ethereumReceiver) {
        this.ethereumReceiver = ethereumReceiver;
    }
}
