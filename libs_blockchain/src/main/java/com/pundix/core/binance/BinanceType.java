package com.pundix.core.binance;

/**
 * @ClassName: BinanceType
 * @Description:
 * @Author: Carl
 * @CreateDate: 2021/3/29 10:40 AM
 */
public enum BinanceType {

    SEND("send"),
    CrossTransferOut("crossTransferOut"),
    CrossTransferOutRelayFee("crossTransferOutRelayFee");
    String name;

    BinanceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
