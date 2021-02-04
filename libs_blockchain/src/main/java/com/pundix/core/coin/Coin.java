package com.pundix.core.coin;


import com.pundix.core.FunctionxNodeConfig;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Bech32;
import org.web3j.utils.Numeric;
import java.io.Serializable;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;

public enum Coin implements Serializable {
    ETHEREUM("ethereum", "", "Ethereum", "ETH", 18, 60, "file:///android_asset/dapp/coin_2.png", "Ethereum", 0xff004167),
    FX_COIN("hub", "fx", "FXCORE", "FXC", 18, 118, "file:///android_asset/coins/coin_1.png", "Function X", 0xff0552DC),
    BITCOIN("bitcoin", "", "Bitcoin", "BTC", 8, 0, "file:///android_asset/coins/coin_7.png", "Bitcoin", 0xffDDA800),
    BINANCE("binance", "", "Binance", "BNB", 8, 714, "file:///android_asset/coins/coin_5.png", "Binance", 0xff333333);

    String id;
    String hrp;
    String name;
    String symbol;
    int decimals;
    int coinType;
    String describe;
    String icon;
    int color;

    Coin(String id, String hrp, String name, String symbol, int decimals, int coinType, String icon, String describe, int color) {
        this.id = id;
        this.hrp = hrp;
        this.name = name;
        this.symbol = symbol;
        this.decimals = decimals;
        this.coinType = coinType;
        this.describe = describe;
        this.icon = icon;
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public int getColor() {
        return color;
    }

    public static Coin getCoin(String value) {
        for (Coin coin : Coin.values()) {
            if (value.toLowerCase().equals(coin.getId().toLowerCase())) {
                return coin;
            }
        }
        return FX_COIN;
    }

    public static Coin getCoinForCoinType(int coinType) {
        for (Coin coin : Coin.values()) {
            if (coinType == coin.getCoinType()) {
                return coin;
            }
        }
        return FX_COIN;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getId() {
        return id;
    }

    public String getHrp() {
        return hrp;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getDecimals() {
        return decimals;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getNodeDerivationPath(int index) {
        return String.format("44H/%sH/1H/0/%s", coinType, index);
    }

    public String getDerivationPath() {
        return "44H/" + coinType + "H/0H/0/%s";
    }

    public String getDerivationPath(int index) {
        return String.format("44H/%sH/0H/0/%s", coinType, index);
    }


    public String getNodeUrl() {
        return FunctionxNodeConfig.getInstance().getNodeConfig(this).getUrl();
    }

    public static boolean isValidAddress(Coin coin, String input) {
        switch (coin) {
            case ETHEREUM:
                return isValidEthAddress(input);
            case BITCOIN:
                return isValidBtcAddress(input);
            case FX_COIN:
            default:
                try {
                    final Bech32.Bech32Data decode = Bech32.decode(input);
                    final String hrp = decode.hrp;
                    return true;
                } catch (Exception e) {
                    return false;
                }

        }
    }

    private static boolean isValidBtcAddress(String input) {
        try {
            Address address = Address.fromString(FunctionxNodeConfig.getInstance().getNetworkParameters(), input);
            if (address != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidEthAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }


}
