package com.pundix.core.ethereum.yfi;


import com.pundix.core.ethereum.EthereumService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;


/**
 * Description：Erc20
 * @author joker
 * @date 2020/5/28
 */
public class YearnService extends EthereumService {
    private static YearnService inchService;

    public static YearnService getInstance() {
        if (inchService == null) {
            inchService = new YearnService();
        }
        return inchService;

    }

    private YearnContract getYearnIearnContract(String fromAdress){
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, fromAdress);
        YearnContract yearnContract = YearnContract.load(YearnContract.IEarnAPR, web3j, readonlyTransactionManager, new DefaultGasProvider());
        return yearnContract;
    }
}
