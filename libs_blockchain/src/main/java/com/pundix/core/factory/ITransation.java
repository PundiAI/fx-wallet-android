package com.pundix.core.factory;

/**
 * Description：ITransation
 * @author Joker
 * @date 2020/5/25
 */
public interface ITransation {

    TransationResult sendTransation(TransationData data) throws Exception;

    String getBalance(String address) throws Exception;
}
