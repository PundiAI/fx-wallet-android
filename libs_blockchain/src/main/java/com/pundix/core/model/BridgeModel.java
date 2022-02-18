package com.pundix.core.model;

import java.io.Serializable;

/**
 * @ClassName: BridgeModel
 * @Description:
 * @Author: YT
 * @CreateDate: 2020/12/25 4:08 PM
 */
public class BridgeModel implements Serializable {
    String fxBridgeContractAddress;
    String tokenContractAddress;

    public String getFxBridgeContractAddress() {
        return fxBridgeContractAddress;
    }

    public String getTokenContractAddress() {
        return tokenContractAddress;
    }

    public void setTokenContractAddress(String tokenContractAddress) {
        this.tokenContractAddress = tokenContractAddress;
    }

    public void setFxBridgeContractAddress(String fxBridgeContractAddress) {
        this.fxBridgeContractAddress = fxBridgeContractAddress;
    }

}
