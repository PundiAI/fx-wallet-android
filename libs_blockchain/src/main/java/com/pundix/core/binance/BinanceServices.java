package com.pundix.core.binance;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.Balance;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * @ClassName: BinanceService
 * @Description:
 * @Author: Joker
 * @Date: 2020/6/15
 */
public class BinanceServices {
    private final String COIN = "BNB";
    public static BinanceServices binanceServices;
    private static BinanceDexApiRestClient client;

    public static BinanceServices getInstance() {
        if (binanceServices == null) {
            binanceServices = new BinanceServices();
        }
        return binanceServices;
    }

    public BinanceServices() {
        client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
    }

    public BigDecimal getAccount(String address) {
        Account account = client.getAccount(address);
        List<Balance> balances = account.getBalances();
        BigDecimal amout = BigDecimal.ZERO;
        for (Balance balance : balances) {
            amout = amout.add(new BigDecimal(balance.getFree()));
        }
        return amout;
    }

    public String transfer(String amount, String fromAddress, String toAddress, String privateKey, boolean sync) throws IOException, NoSuchAlgorithmException {
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setCoin(COIN);
        transfer.setFromAddress(fromAddress);
        transfer.setToAddress(toAddress);
        Wallet wallet = new Wallet(privateKey, BinanceDexEnvironment.TEST_NET);
        TransactionOption transactionOption = TransactionOption.DEFAULT_INSTANCE;
        List<TransactionMetadata> transfer1 = client.transfer(transfer, wallet, transactionOption, sync);
        return transfer1.get(0).getHash();

    }

}

