package com.pundix.core.bitcoin.service;



import com.alibaba.fastjson.JSON;
import com.pundix.core.bitcoin.model.BitcoinAccountModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;


import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;


/**
 * @ClassName: BitcoinRpcService
 * @Author: Joker
 * @CreateDate: 2019-11-06 16:24
 */
public class BitcoinHttp {
    public static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain; charset=utf-8");

    public static Call<BitcoinAccountModel> getAccountUtxo(String zpub) {
        return BitcoinService.getFunctionxClient().getAccountUtxo(zpub);
    }

    public static Call<BitcoinResultModel> sendTransaction(@Body String tx) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("tx",tx);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(hashMap));
        return BitcoinService.getFunctionxClient().sendTransaction(requestBody);
    }

}
