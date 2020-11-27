package com.pundix.core.bitcoin.service;



import com.pundix.core.bitcoin.model.BitcoinAccountModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @ClassName: BitcoinRpcClient
 * @Author: Joker
 * @CreateDate: 2019-11-06 16:24
 */

public interface BitcoinClient {

    @GET("/unspent")
    Call<BitcoinAccountModel> getAccountUtxo(@Query("active") String address);

    @POST("https://api.blockcypher.com/v1/btc/main/txs/push?token=4a3fb04508724090b5d9bed646662bb4")
    Call<BitcoinResultModel> sendTransaction(@Body RequestBody requestBody);


}
