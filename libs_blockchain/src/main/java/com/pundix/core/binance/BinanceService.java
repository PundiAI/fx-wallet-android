package com.pundix.core.binance;


import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.Balance;
import com.binance.dex.api.client.domain.Fees;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import com.pundix.core.FunctionxNodeConfig;
import com.pundix.core.coin.Coin;
import com.pundix.core.coin.ServiceChainType;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * @ClassName: BinanceService
 * @Description:
 * @Author: Carl
 * @Date: 2020/6/15
 */
public class BinanceService {
    public static BinanceService binanceServices;
    private static BinanceDexApiRestClient client;
    private static BinanceDexApiNodeClient nodeClient;

    public static BinanceService getInstance() {
        if (binanceServices == null) {
            binanceServices = new BinanceService();
        }
        return binanceServices;
    }

    public BinanceService() {
        if (FunctionxNodeConfig.getInstance().getNodeChainType(Coin.BINANCE) == ServiceChainType.BINANCE_BEP_MAINNET.getChainType()) {
            client = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.PROD.getNodeUrl(), BinanceDexEnvironment.PROD.getHrp(), BinanceDexEnvironment.PROD.getValHrp());
            nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.PROD.getNodeUrl(), BinanceDexEnvironment.PROD.getHrp(), BinanceDexEnvironment.PROD.getValHrp());
        } else {
            client = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.TEST_NET.getNodeUrl(), BinanceDexEnvironment.TEST_NET.getHrp(), BinanceDexEnvironment.TEST_NET.getValHrp());
            nodeClient = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(BinanceDexEnvironment.TEST_NET.getNodeUrl(), BinanceDexEnvironment.TEST_NET.getHrp(), BinanceDexEnvironment.TEST_NET.getValHrp());
        }

    }

    public String getAccount(String symbol, String address) {
        Account account = client.getAccount(address);
        String amount = "0";
        if (account != null) {
            List<Balance> balances = account.getBalances();
            for (Balance balance : balances) {
                if (balance.getSymbol().contains(symbol)) {
                    amount = balance.getFree();
                }
            }
        }
        return amount;
    }


    public TransactionMetadata transfer(String amount, String symbol, String toAddress, String privateKey) throws IOException, NoSuchAlgorithmException {
        Wallet wallet;
        if (FunctionxNodeConfig.getInstance().getNodeChainType(Coin.BINANCE) == ServiceChainType.BINANCE_BEP_MAINNET.getChainType()) {
            wallet = new Wallet(privateKey, BinanceDexEnvironment.PROD);
        } else {
            wallet = new Wallet(privateKey, BinanceDexEnvironment.TEST_NET);
        }

        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setCoin(symbol);
        transfer.setFromAddress(wallet.getAddress());
        transfer.setToAddress(toAddress);
        TransactionOption transactionOption = TransactionOption.DEFAULT_INSTANCE;
        List<TransactionMetadata> transfer1 = client.transfer(transfer, wallet, transactionOption, true);
        return transfer1.get(0);

    }

    public List<Fees> getFees() {
        List<Fees> feesList = client.getFees();
        return feesList;
    }

}

