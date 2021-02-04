package com.pundix.core.ethereum;

import android.text.TextUtils;

import com.pundix.core.factory.TransationData;

/**
 * Description：
 * @author joker
 * @date 2020/5/25
 */
public class EthereumTransationData extends TransationData {

    private static final long serialVersionUID = 1906792461389715166L;
    private String fromAddress;
    private String toAddress;
    private String privateKey;
    private String gasPrice;
    private String gasLimit;
    private String data;
    private String value;
    private String nonce;

    private String extendPublicKey;

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

    public String getExtendPublicKey() {
        return extendPublicKey;
    }

    public void setExtendPublicKey(String extendPublicKey) {
        this.extendPublicKey = extendPublicKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
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

    public String getData() {
        if(TextUtils.isEmpty(data)){
            return "";
        }
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UnsignTransationData{" +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", extendPublicKey='" + extendPublicKey + '\'' +
                '}';
    }
}
