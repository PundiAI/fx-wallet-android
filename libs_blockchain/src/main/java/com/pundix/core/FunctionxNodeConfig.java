package com.pundix.core;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pundix.common.utils.ContextUtil;
import com.pundix.common.utils.GsonUtils;
import com.pundix.common.utils.PreferencesUtil;
import com.pundix.common.utils.SystemUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.model.LocalCoinModel;
import com.pundix.core.model.NodeModel;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionxNodeConfig {

    private static FunctionxNodeConfig functionxConfig;
    private HashMap<String, ArrayList<NodeModel>> nodeInfoMap;
    private Context mContext;

    public static final String KEY_COIN_RESOURCES = "coin_resources";
    public static final String KEY_COIN = "key_data";

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
            nodeInfoMap = new GsonBuilder().create().fromJson(nodeList, new TypeToken<HashMap<String, ArrayList<NodeModel>>>() {
            }.getType());
            final List<LocalCoinModel> coinModelList = getAllCoinModel();
            for (LocalCoinModel coinModel : coinModelList) {
                for (Map.Entry<String, ArrayList<NodeModel>> entry : nodeInfoMap.entrySet()) {
                    Coin coin = Coin.getCoin(entry.getKey());
                    if (coinModel.getChainType() == coin.getChainType()) {
                        if (entry.getValue().size() == 0) {
                            continue;
                        }
                        List<LocalCoinModel> coinModelList1 = entry.getValue().get(getNodeIndex(coin)).getLocalCoinModelList();
                        if (coinModelList1 == null) {
                            coinModelList1 = new ArrayList<>();
                        }
                        coinModelList1.add(coinModel);
                        entry.getValue().get(getNodeIndex(coin)).setLocalCoinModelList(coinModelList1);
                    }
                }
            }
        }
    }

    public NetworkParameters getNetworkParameters() {
        return TestNet3Params.get();
    }

    public List<LocalCoinModel> getAllCoinModel() {
        String token = PreferencesUtil.getStringData(mContext, KEY_COIN_RESOURCES, KEY_COIN);
        if (TextUtils.isEmpty(token)) {
            token = SystemUtils.getAssetsString("token/tokens_" + getNodeConfig(Coin.ETHEREUM).getName() + ".json", mContext);
        }
        List<LocalCoinModel> coinModelList = GsonUtils.fromJson(token, new TypeToken<List<LocalCoinModel>>() {
        }.getType());
        List<LocalCoinModel> removeModel = new ArrayList<>();
        for (LocalCoinModel coinModel : coinModelList) {
            if (coinModel.getStatus() == 2) {
                removeModel.add(coinModel);
            }
        }
        for (LocalCoinModel coinModel : removeModel) {
            coinModelList.remove(coinModel);
        }
        return coinModelList;
    }


    public NodeModel getNodeConfig(Coin coin) {
        NodeModel nodeModel = null;
        nodeModel = nodeInfoMap.get(coin.getId()).get(getNodeIndex(coin));
        return nodeModel;
    }

    public boolean isMain() {
        if ((BuildConfig.BUILD_TYPE.contains("uat") || BuildConfig.BUILD_TYPE.contains("release"))) {
            return true;
        }
        return false;
    }

    private int getNodeIndex(Coin coin) {
        int index = 0;
        if (isMain() && coin == Coin.ETHEREUM) {
            index = 1;
        }
        return index;
    }
}
