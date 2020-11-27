package com.pundix.core.binance;

import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: BinanceTransaction
 * @Author: Joker
 * @Date: 2020/6/15
 */
public class BinanceTransaction implements ITransation {
    @Override
    public TransationResult sendTransation(TransationData data) {
        TransationResult resp = new TransationResult();
        //fee 37500
        try {
            String transfer = BinanceServices.getInstance().transfer(data.getValue(), data.getFromAddress(), data.getToAddress(), data.getPrivateKey(), false);
            resp.setCode(0);
            resp.setHash(transfer);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            resp.setCode(-1);
        }
        return resp;
    }

    @Override
    public String getBalance(String address) {
        return null;
    }
}
