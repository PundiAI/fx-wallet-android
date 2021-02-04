package com.pundix.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.pundix.common.autoservice.ServiceFactory;
import com.pundix.common.utils.ContextUtil;
import com.pundix.common.utils.GsonUtils;
import com.pundix.common.utils.PreferencesUtil;
import com.pundix.common.utils.SystemUtils;
import com.pundix.core.coin.Coin;
import com.pundix.core.coin.ServiceChainType;
import com.pundix.core.model.LocalCoinModel;
import com.pundix.core.model.NodeModel;

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
 * @Author: yangtao
 * @CreateDate: 2020/11/25 6:13 PM
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
            nodeInfoMap = GsonUtils.fromJson(nodeList, new TypeToken<Map<String, List<NodeModel>>>() {
            }.getType());
            final List<LocalCoinModel> coinModelList = getAllCoinModel();

            for (Map.Entry<String, ArrayList<NodeModel>> entry : nodeInfoMap.entrySet()) {
                final Coin coin = Coin.getCoin(entry.getKey());
                for (LocalCoinModel localCoinModel : coinModelList) {
                    final ArrayList<NodeModel> entryValue = entry.getValue();
                    for (NodeModel nodeModel : entryValue) {
                        //链一样
                        if (entry.getKey().equals(Coin.getCoinForCoinType(localCoinModel.getCoinType()).getName().toLowerCase())) {
                            ServiceChainType chainType = ServiceChainType.getChainType(localCoinModel.getChainType());
                            //对比环境
                            if (chainType.getChainType()==(getNodeChainType(coin))) {
                                List<LocalCoinModel> localCoinModelList = nodeModel.getLocalCoinModelList();
                                if (localCoinModelList == null) {
                                    localCoinModelList = new ArrayList<>();
                                    nodeModel.setLocalCoinModelList(localCoinModelList);
                                }
                                localCoinModelList.add(localCoinModel);
                                nodeModel.setLocalCoinModelList(localCoinModelList);
                            }
                        }
                    }

                }
            }
        }
    }

    public NetworkParameters getNetworkParameters() {

        if (getNodeChainType(Coin.BITCOIN)== ServiceChainType.BITCOIN_MAINNET.getChainType()) {
            return MainNetParams.get();
        }
        return TestNet3Params.get();
    }


    public List<LocalCoinModel> getAllCoinModel() {
        List<LocalCoinModel> coinModelList = getCoinResources();
        List<LocalCoinModel> removeModel = new ArrayList<>();
        for (LocalCoinModel coinModel : coinModelList) {
            if (coinModel.getStatus() == 2) {
                removeModel.add(coinModel);
            }
        }
        for (LocalCoinModel coinModel : removeModel) {
            coinModelList.remove(coinModel);
        }

        List<LocalCoinModel> newData = new ArrayList<>();
        for (LocalCoinModel localCoinModel : coinModelList) {
            Coin coin = Coin.getCoinForCoinType(localCoinModel.getCoinType());
            if (localCoinModel.getChainType()==(getNodeChainType(coin))) {
                newData.add(localCoinModel);
            }
        }
        return newData;
    }

    public List<LocalCoinModel> getCoinResources() {
        //获取币种列表
        String token = ServiceFactory.getInstance().getCoinResourcesConfigure().getCoinResources();
        if (TextUtils.isEmpty(token)) {
            token = SystemUtils.getAssetsString("token/coin.json", mContext);
        }
        List<LocalCoinModel> data = GsonUtils.fromJson(token, new TypeToken<List<LocalCoinModel>>() {
        }.getType());

        return data;
    }


    public NodeModel getNodeConfig(Coin coin) {
        NodeModel nodeModel = null;
        final ArrayList<NodeModel> nodeModels = nodeInfoMap.get(coin.getId());
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

    private boolean isMain() {
        if ((BuildConfig.BUILD_TYPE.contains("uat") || BuildConfig.BUILD_TYPE.contains("release"))) {
            return true;
        }
        return false;
    }

    public boolean isEthereumMain() {
        if (getNodeChainType(Coin.ETHEREUM)== ServiceChainType.ETHEREUM_MAINNET.getChainType()) {
            return true;
        }
        return false;
    }

    public int getNodeChainType(Coin coin) {
        String chain = PreferencesUtil.getStringData(mContext, KEY_CHAIN_SELECT);
        Map<String, Integer> chainMap = new HashMap<>();
        if (TextUtils.isEmpty(chain)) {
            if (isMain()) {
                chainMap.put("bitcoin", ServiceChainType.BITCOIN_TESTNET3.getChainType());
                chainMap.put("ethereum", ServiceChainType.ETHEREUM_KOVAN.getChainType());
                chainMap.put("binance", ServiceChainType.BINANCE_TESTNET.getChainType());
                chainMap.put("hub", ServiceChainType.FXCLOUD_TESTNET.getChainType());
            } else {
                chainMap.put("bitcoin", ServiceChainType.BITCOIN_TESTNET3.getChainType());
                chainMap.put("ethereum", ServiceChainType.ETHEREUM_KOVAN.getChainType());
                chainMap.put("binance", ServiceChainType.BINANCE_TESTNET.getChainType());
                chainMap.put("hub", ServiceChainType.FXCLOUD_TESTNET.getChainType());
            }
            PreferencesUtil.saveStringData(mContext, KEY_CHAIN_SELECT, GsonUtils.toJson(chainMap));
        } else {
            chainMap = GsonUtils.fromJson(chain, new TypeToken<Map<String, Integer>>() {
            }.getType());
        }
        return chainMap.get(coin.getId());
    }

    public static final String KEY_CHAIN_SELECT = "coin_chain_select";
    public static final String KEY_COIN_RESOURCES = "coin_resources_v1";
    public static final String KEY_COIN = "key_data";
}
