package com.pundix.core;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import com.pundix.common.utils.ContextUtil;
import com.pundix.common.utils.GsonUtils;
import com.pundix.common.utils.PreferencesUtil;
import com.pundix.common.utils.SystemUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.coin.ServiceChainType;

import com.pundix.core.ethereum.model.NodeModel;


import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FunctionxNodeConfig
 * @Description:
 * @Author: Carl
 * @CreateDate: 2020/11/25 6:13
 */
public class FunctionxNodeConfig {

    private static FunctionxNodeConfig functionxConfig;

    Map<String, ArrayList<NodeModel>> nodeInfoMap;
    private Context mContext;


    public static FunctionxNodeConfig getInstance() {
        if (functionxConfig == null) {
            functionxConfig = new FunctionxNodeConfig();
        }
        return functionxConfig;
    }


    public FunctionxNodeConfig() {
        mContext = ContextUtil.getContext();
        if (nodeInfoMap == null) {
            String nodeList = SystemUtils.getAssetsString("node/nodeConfig.json", mContext);
            nodeInfoMap = GsonUtils.fromJson(nodeList, new TypeToken<Map<String, List<NodeModel>>>() {}.getType());
            for (Coin coin:Coin.values()){
                getNodeChainType(coin);
            }
        }
    }

    public NetworkParameters getNetworkParameters() {
        if (getNodeChainType(Coin.BITCOIN) == ServiceChainType.BITCOIN_MAINNET.getChainType()) {
            return MainNetParams.get();
        }
        return TestNet3Params.get();
    }



    public NodeModel getNodeConfig(Coin coin) {
        NodeModel nodeModel = null;
        ArrayList<NodeModel> nodeModels = nodeInfoMap.get(coin.getId());
        for (NodeModel nodeModel1 : nodeModels) {
            final int nodeChainType = getNodeChainType(coin);
            ServiceChainType chainType = ServiceChainType.getChainType(nodeChainType);
            if (nodeModel1.getName().toLowerCase().equals(chainType.getName().toLowerCase())) {
                nodeModel = nodeModel1;
                break;
            }
        }
        return nodeModel;
    }



    public boolean isEthereumMain() {
        if (getNodeChainType(Coin.ETHEREUM) == ServiceChainType.ETHEREUM_MAINNET.getChainType()) {
            return true;
        }
        return false;
    }

    public Map<String, Integer> getDefuletNodeMap() {
        Map<String, Integer> chainMap = new HashMap<>();
        chainMap.put(Coin.BITCOIN.getId(), ServiceChainType.BITCOIN_MAINNET.getChainType());
        chainMap.put(Coin.ETHEREUM.getId(), ServiceChainType.ETHEREUM_MAINNET.getChainType());
        chainMap.put(Coin.BINANCE.getId(), ServiceChainType.BINANCE_BEP_TESTNET.getChainType());
        chainMap.put(Coin.FX_COIN.getId(), ServiceChainType.FXCORE_MAINNET.getChainType());
        chainMap.put(Coin.BINANCE_BSC.getId(), ServiceChainType.BINANCE_BSC_TESTNET.getChainType());
        chainMap.put(Coin.FX_PAYMENT.getId(), ServiceChainType.FX_PAYMENT_TESTNET.getChainType());
        return chainMap;
    }

    public int getNodeChainType(Coin coin) {
        String chain = PreferencesUtil.getStringData(mContext, KEY_CHAIN_SELECT);
        Map<String, Integer> chainMap = new HashMap<>();
        if (TextUtils.isEmpty(chain)) {
            chainMap = getDefuletNodeMap();
            PreferencesUtil.saveStringData(mContext, KEY_CHAIN_SELECT, GsonUtils.toJson(chainMap));
        } else {
            chainMap = GsonUtils.fromJson(chain, new TypeToken<Map<String, Integer>>() {}.getType());
        }
        if(chainMap.size() != Coin.values().length){
            for (Coin coin1:Coin.values()){
                final Integer integer = chainMap.get(coin1.getId());
                if(integer == null){
                    final Integer integer1 = getDefuletNodeMap().get(coin1.getId());
                    chainMap.put(coin1.getId(), integer1);
                }
            }
            PreferencesUtil.saveStringData(mContext, KEY_CHAIN_SELECT, GsonUtils.toJson(chainMap));
        }

        return  chainMap.get(coin.getId());
    }

    public static final String KEY_CHAIN_SELECT = "coin_chain_select";
}
