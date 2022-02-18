package com.pundix.core.model;

/**
 * @ClassName: TransactionEth
 * @Description:
 * @Author: pundix002
 * @Date: 4/12/21
 */
public class TransactionEth {
    private String status;
    private String gasUsed;
    private String gasPrice;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }
}
