package com.pundix.core.coin;

public enum ServiceChainType {
    ETHEREUM_MAINNET(0, "Mainnet", "ethereum",true),
    BITCOIN_MAINNET(1, "Mainnet", "bitcoin",true),
    BINANCE_MAINNET(2, "Mainnet", "binance",true),
    FXCLOUD_MAINNET(3, "Mainnet", "binance",true),
    ETHEREUM_KOVAN(20, "Kovan", "ethereum",false),
    BITCOIN_TESTNET3(21, "Testnet3", "bitcoin",false),
    BINANCE_TESTNET(22, "Testnet", "hub",false),
    FXCLOUD_TESTNET(23, "Testnet", "hub",false),
    FXCLOUD_TEST(24, "Test", "hub",false);

    int chainType;
    String name;
    String coin;
    boolean isMain;

    ServiceChainType(int chainType, String name, String coin,boolean isMain) {
        this.chainType = chainType;
        this.name = name;
        this.coin = coin;
        this.isMain = isMain;
    }

    public static ServiceChainType getChainType(int chainType) {
        for (ServiceChainType serviceChainType : ServiceChainType.values()) {
            if (chainType == serviceChainType.getChainType()) {
                return serviceChainType;
            }
        }
        return null;
    }

    public static ServiceChainType getChainType(String name, String coin) {
        for (ServiceChainType serviceChainType : ServiceChainType.values()) {
            if (name.toLowerCase().equals(serviceChainType.getName().toLowerCase()) && serviceChainType.getCoin().toLowerCase().equals(coin.toLowerCase())) {
                return serviceChainType;
            }
        }
        return null;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChainType() {
        return chainType;
    }

    public void setChainType(int chainType) {
        this.chainType = chainType;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}
