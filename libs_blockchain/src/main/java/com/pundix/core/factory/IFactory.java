package com.pundix.core.factory;


import com.pundix.core.coin.Coin;

/**
 * Description：
 *
 * @author Carl
 * @date 2020/5/25
 */
public abstract class IFactory {

    public abstract <T extends ITransation> T createTransationFactory(Coin coin);

}
