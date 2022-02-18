package com.pundix.core.fxcore;

import com.pundix.core.coin.Coin;
import com.pundix.core.factory.TransationData;
import com.pundix.core.model.AmountModel;
import com.pundix.core.model.FxData;


public class FunctionXTransationData extends TransationData {

    private static final long serialVersionUID = -4138198585534621579L;

    private Coin coin;
    private String chainId;
    private String url;
    private String hrp;
    private String routeUrl;
    private String hash;
    private AmountModel amount;
    private String privateKey;
    private String fromAddress;
    private String toAddress;
    private String gasPrice;
    private String gasLimit;
    private FxData fxData;

    public String getHash() {
        return hash;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public FxData getFxData() {
        return fxData;
    }

    public void setFxData(FxData fxData) {
        this.fxData = fxData;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

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

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHrp() {
        return hrp;
    }

    public void setHrp(String hrp) {
        this.hrp = hrp;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
