package com.pundix.core.bitcoin.service;


import com.alibaba.fastjson.JSON;
import com.pundix.core.FunctionxNodeConfig;
import com.pundix.core.bitcoin.model.BitcoinBalanceModel;
import com.pundix.core.bitcoin.model.BitcoinBlockTxModel;
import com.pundix.core.bitcoin.model.BitcoinResultModel;
import com.pundix.core.coin.Coin;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;


/**
 * @ClassName: BitcoinRpcService
 * @Author: YT
 * @CreateDate: 2019-11-06 16:24
 */
public class BitcoinHttp {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    public static Call<BitcoinBalanceModel> getBalance(String address) {
        return BitcoinService.getFunctionxClient().getBalance(getBtcName(),address);
    }
    public static Call<List<BitcoinBalanceModel>> getBalanceArray(String address) {
        return BitcoinService.getFunctionxClient().getBalanceArray(getBtcName(),address);
    }

    public static Call<BitcoinBalanceModel> getAccountUtxo(String address) {
        return BitcoinService.getFunctionxClient().getAccountUtxo(getBtcName(),address, true, true);
    }
    public static Call<BitcoinBlockTxModel> getTxs(String hash){
        return BitcoinService.getFunctionxClient().getTxs(getBtcName(),hash);
    }


    private static String getBtcName(){
        String name = FunctionxNodeConfig.getInstance().getNodeConfig(Coin.BITCOIN).getName();
        if(name.equals("Testnet3")){
            return "test3";
        }else {
            return "main";
        }
    }


    public static Call<BitcoinResultModel> sendTransaction(@Body String tx) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("tx",tx);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(hashMap));
        return BitcoinService.getFunctionxClient().sendTransaction(getBtcName(),requestBody);
    }

}
