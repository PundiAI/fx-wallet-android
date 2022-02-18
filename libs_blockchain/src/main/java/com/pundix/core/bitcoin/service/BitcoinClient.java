package com.pundix.core.bitcoin.service;


import com.pundix.core.bitcoin.model.BitcoinBalanceModel;
import com.pundix.core.bitcoin.model.BitcoinBlockTxModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @ClassName: BitcoinRpcClient
 * @Author: YT
 * @CreateDate: 2019-11-06 16:24
 */

public interface BitcoinClient {
    @GET("/v1/btc/{network}/addrs/{address}/balance")
    Call<BitcoinBalanceModel> getBalance(@Path("network") String network,@Path("address") String address);
    @GET("/v1/btc/{network}/addrs/{address}/balance")
    Call<List<BitcoinBalanceModel>> getBalanceArray(@Path("network") String network,@Path("address") String address);

    @GET("/v1/btc/{network}/addrs/{address}")
    Call<BitcoinBalanceModel> getAccountUtxo(@Path("network") String network,@Path("address") String address,@Query("unspentOnly") Boolean unspentOnly,@Query("includeScript") Boolean includeScript);

    @POST("/v1/btc/{network}/txs/push?token=ac878fcc52eb4bfc9ef43d1bd9b6dfe7")
    Call<BitcoinResultModel> sendTransaction(@Path("network") String network,@Body RequestBody requestBody);

    @GET("/v1/btc/{network}/txs/{hash}")
    Call<BitcoinBlockTxModel> getTxs(@Path("network") String network, @Path("hash")String hash);
}
