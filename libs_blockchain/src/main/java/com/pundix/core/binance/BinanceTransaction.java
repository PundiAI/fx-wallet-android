package com.pundix.core.binance;

import android.util.Log;

import com.binance.dex.api.client.domain.Fees;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.pundix.core.factory.ITransation;
import com.pundix.core.factory.TransationData;
import com.pundix.core.factory.TransationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BinanceTransaction
 * @Description:
 * @Author: Carl
 * @Date: 2020/6/15
 */
public class BinanceTransaction implements ITransation {

    @Override
    public TransationResult sendTransation(TransationData data) throws Exception {
        BinanceTransationData binanceTransationData = (BinanceTransationData) data;
        TransationResult resp = new TransationResult();
        final TransactionMetadata transfer = BinanceService.getInstance().transfer(binanceTransationData.getAmount(), binanceTransationData.getSymbol(), binanceTransationData.getToAddress(), binanceTransationData.getPrivateKey());
        if (transfer.getCode() == 0) {
            resp.setCode(0);
            resp.setHash(transfer.getHash());
        } else {
            resp.setCode(transfer.getCode());
            resp.setMsg(transfer.getLog());
        }
        return resp;
    }

    @Override
    public String getBalance(String... address) {
        return null;
    }

    @Override
    public List<String> getArrayBalance(String... address) {
        return null;
    }


    public String getBinanceBalance(String symbol, String... address) {
        return BinanceService.getInstance().getAccount(symbol, address[0]);
    }


    public List<String> getBinanceArrayBalance(String symbol, String... address) {
        List<String> array = new ArrayList<>();
        for (String add : address) {
            try {
                array.add(BinanceService.getInstance().getAccount(symbol, address[0]));
            } catch (Exception e) {
                e.printStackTrace();
                array.add(null);
            }
        }
        return array;
    }

    @Override
    public Object getFee(TransationData data) throws Exception {
        final List<Fees> fees = BinanceService.getInstance().getFees();
        BinanceTransationData binanceTransationData = (BinanceTransationData) data;
        for (Fees fee : fees) {
            if (fee.getFixedFeeParams() != null && fee.getFixedFeeParams().getMsgType().equals(binanceTransationData.getBinanceType().getName())) {
                return "" + fee.getFee();
            }
            if (fee.getMsgType() != null && fee.getMsgType().equals(binanceTransationData.getBinanceType().getName())) {
                return "" + fee.getFee();
            }
        }
        return "" + 7500;
    }

    @Override
    public Object getTxs(Object data) {
        return null;
    }
}
