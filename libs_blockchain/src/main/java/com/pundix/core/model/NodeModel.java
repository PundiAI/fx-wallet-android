package com.pundix.core.model;

import java.io.Serializable;
import java.util.List;

public class NodeModel implements Serializable {
    private static final long serialVersionUID = -2038952718732433234L;
    private String name;
    private String url;
    private String wsurl;
    private String bridgeBank;
    private String chainId;
    private String blockUrl;
    private List<LocalCoinModel> localCoinModelList;

    public String getWsurl() {
        return wsurl;
    }

    public void setWsurl(String wsurl) {
        this.wsurl = wsurl;
    }

    public String getBlockUrl() {
        return blockUrl;
    }

    public void setBlockUrl(String blockUrl) {
        this.blockUrl = blockUrl;
    }

    public List<LocalCoinModel> getLocalCoinModelList() {
        return localCoinModelList;
    }

    public void setLocalCoinModelList(List<LocalCoinModel> localCoinModelList) {
        this.localCoinModelList = localCoinModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBridgeBank() {
        return bridgeBank;
    }

    public void setBridgeBank(String bridgeBank) {
        this.bridgeBank = bridgeBank;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }
}
