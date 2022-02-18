package com.pundix.core.factory;


import com.pundix.core.binance.BinanceTransaction;
import com.pundix.core.binancebsc.BinancebscTransation;
import com.pundix.core.bitcoin.BitcoinjTransation;
import com.pundix.core.coin.Coin;
import com.pundix.core.ethereum.EthereumService;
import com.pundix.core.ethereum.EthereumTransation;
import com.pundix.core.fxcore.FxCoreTransation;


/**
 * Description：
 *
 * @author chenyin
 * @date 2020/5/26
 */
public class TransationFactory extends IFactory {

    public static TransationFactory getInstance() {
        return new TransationFactory();
    }


    @Override
    public <T extends ITransation> T createTransationFactory(Coin coin) {
        ITransation iTransation = null;
        switch (coin) {
            case BITCOIN:
                iTransation = new BitcoinjTransation();
                break;
            case ETHEREUM:
                iTransation = new EthereumTransation();
                break;
            case FX_PAYMENT:
            case FX_COIN:
                iTransation = new FxCoreTransation();
                break;

            case BINANCE:
                iTransation = new BinanceTransaction();
                break;
            case BINANCE_BSC:
                iTransation = new BinancebscTransation();
                break;
        }
        return (T) iTransation;
    }


    public EthereumService getEthereumService() {
        return EthereumService.getInstance();
    }


}
