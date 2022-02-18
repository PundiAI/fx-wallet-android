package com.pundix.core.binance;

import com.pundix.core.factory.TransationData;

/**
 * BinanceTransationData
 * Description：
 * @author Carl
 * @date 2020/5/25
 */
public class BinanceTransationData extends TransationData {

    private static final long serialVersionUID = 1906792461389715166L;
    private String fromAddress;
    private String toAddress;
    private String privateKey;
    private String amount;
    private String symbol;
    private String fee;
    private BinanceType binanceType;

    public BinanceType getBinanceType() {
        return binanceType;
    }

    public void setBinanceType(BinanceType binanceType) {
        this.binanceType = binanceType;
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


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "UnsignTransationData{" +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
