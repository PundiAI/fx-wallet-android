package com.pundix.core.model;

import java.io.Serializable;

/**
 * @ClassName: AmountModel
 * @Description:
 * @Author: YT
 * @CreateDate: 2020/12/31 7:22 PM
 */
public class AmountModel implements Serializable {
    private static final long serialVersionUID = 6454557554391383207L;
    private String amount;
    private String denom;
    private int decimals;
    private String buyAmount;
    private String sellAmount;
    private String sellSymbol;
    private String buySymbol;
    private int buyDecimals;

    public AmountModel(){

    }

    public String getSellSymbol() {
        return sellSymbol;
    }

    public void setSellSymbol(String sellSymbol) {
        this.sellSymbol = sellSymbol;
    }

    public AmountModel(String amount, String denom) {
        this.amount = amount;
        this.denom = denom;

    }
    public AmountModel(String amount, String denom, int decimals) {
        this.amount = amount;
        this.denom = denom;
        this.decimals = decimals;
    }

    public int getBuyDecimals() {
        return buyDecimals;
    }

    public void setBuyDecimals(int buyDecimals) {
        this.buyDecimals = buyDecimals;
    }

    public String getBuySymbol() {
        return buySymbol;
    }

    public void setBuySymbol(String buySymbol) {
        this.buySymbol = buySymbol;
    }

    public String getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(String buyAmount) {
        this.buyAmount = buyAmount;
    }

    public String getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(String sellAmount) {
        this.sellAmount = sellAmount;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDenom() {
        return denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }
}
