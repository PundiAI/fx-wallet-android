package com.pundix.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LocalCoinModel {

    @SerializedName("icon")
    private String img;
    int chainType;
    int decimals;
    @SerializedName("unit")
    private String symbol;
    int status;
    @SerializedName(value = "contractAddress")
    private String contract = "";
    @SerializedName("name")
    private String title;
    int coinType;
    int sort;
    int symbolId;
    int currencyId;
    int version;
    private boolean isAdd;

    public int getChainType() {
        return chainType;
    }

    public void setChainType(int chainType) {
        this.chainType = chainType;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(int symbolId) {
        this.symbolId = symbolId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getVersion() {
        return version;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LocalCoinModel)) {
            return false;
        }

        LocalCoinModel re = (LocalCoinModel) obj;

        return (getCurrencyId() == re.getCurrencyId()
                && getTitle().equals(re.getTitle())
                && getSymbol().equals(re.getSymbol())
                && getImg().equals(re.getImg())
                && getDecimals() == re.getDecimals()
                && getSymbolId() == re.getSymbolId()
                && getChainType() == re.getChainType()
                && getCoinType() == re.getCoinType()
                && getSort() == re.getSort()
                && getStatus() == re.getStatus()
                && getContract().equals(re.getContract()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrencyId(),getTitle(),getSymbol(),getImg(),getDecimals(),getSymbolId(),getChainType(),getCoinType(),getSort(),getStatus(),getContract());
    }
}
