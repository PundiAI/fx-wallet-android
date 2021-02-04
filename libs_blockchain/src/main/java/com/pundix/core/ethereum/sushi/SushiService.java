package com.pundix.core.ethereum.sushi;


import com.pundix.core.ethereum.EthereumService;

import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: SushiService
 * @Description:
 * @Author: Joker
 * @CreateDate: 2020/12/2
 */
public class SushiService extends EthereumService {
    private static final String TAG = "SushiService";
    private volatile static SushiService sushiService;

    public static SushiService getInstance() {
        if (sushiService == null) {
            synchronized (SushiService.class) {
                sushiService = new SushiService();
            }
        }
        return sushiService;
    }

    public RemoteCall<List<Type>> getWethAddress(String contractAddress, String address) throws IOException {
        Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/99cbdba1bfdc4080886fe079dc1aef33"));
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        SushiContract sushiContract = SushiContract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return sushiContract.WETH();
    }

    public RemoteCall<List<Type>> getTotalAmount(String contractAddress, String address){
        Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/99cbdba1bfdc4080886fe079dc1aef33"));
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, address);
        SushiContract sushiContract = SushiContract.load(contractAddress, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return sushiContract.totalAmount(address);
    }

}
