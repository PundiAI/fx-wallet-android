package com.pundix.core.factory;

import java.util.List;

/**
 * Description：ITransation
 * @author Carl
 * @date 2020/5/25
 */
public interface ITransation {

    TransationResult sendTransation(TransationData data) throws Exception;

    String getBalance(String... address) ;

    List<String> getArrayBalance(String... address) ;


    Object getFee(TransationData data) throws Exception;


    Object getTxs(Object data);
}
