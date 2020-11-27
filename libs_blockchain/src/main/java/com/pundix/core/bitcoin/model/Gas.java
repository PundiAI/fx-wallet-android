package com.pundix.core.bitcoin.model;

import java.io.Serializable;

/**
 * @ClassName: Gas
 * @Description:
 * @Author: Joker
 * @CreateDate: 2020/8/6 3:08 PM
 */
public class Gas implements Serializable {
    private static final long serialVersionUID = 5849856745409547659L;
    String gasPrice;
    String gasLimit;

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

    @Override
    public String toString() {
        return "GasModel{" +
                "gasPrice='" + gasPrice + '\'' +
                ", gasLimit='" + gasLimit + '\'' +
                '}';
    }
}
