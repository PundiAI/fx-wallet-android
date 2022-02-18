package com.pundix.core.coin;


import com.pundix.common.constants.BitcoinUtil;
import com.pundix.core.FunctionxNodeConfig;
import com.pundix.core.R;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Bech32;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.web3j.utils.Numeric;

import java.io.Serializable;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;

/**
 * @ClassName: Coin
 * @Description:
 * @Author: Carl
 * @CreateDate: 2021/5/27 2:16 PM
 */
public enum Coin implements Serializable {
    ETHEREUM("ethereum", "", "Ethereum", "ETH", 18, 60, "Ethereum", R.drawable.icon_chain_ethereum_512,R.drawable.icon_chain_black_ethereum_32,R.drawable.home_ethereum_chain_w, 0xff004167),
    BINANCE_SMART_CHAIN("bsc", "", "BinanceSmartChain", "BNB", 18, 60, "BSC", R.drawable.icon_chain_bsc_512,R.drawable.icon_chain_black_bsc_32,R.drawable.home_bsc_chain_w, 0xff333333),
    FX_COIN("fxcore", "fx", "FX", "FX", 18, 118, "f(x)Core", R.drawable.icon_chain_fxcore_512,R.drawable.icon_chain_black_funcitonx_32,R.drawable.home_fx_chain_w ,0xff0552DC),
    FX_PUNDIX("pundix", "px", "PUNDIX", "PUNDIX", 18, 118, "Pundi X Chain", R.drawable.icon_chain_fxcore_512,R.drawable.icon_chain_black_pundix_32,R.drawable.home_pundix_chain_w, 0xff0552DC),
    FX_DEX("fxdex", "dex", "Variable", "FX", 18, 118, "Variable", R.drawable.icon_chain_variable_32,R.drawable.icon_chain_black_variable, R.drawable.home_variablefff,0xff0552DC),
    BITCOIN("bitcoin", "", "Bitcoin", "BTC", 8, 0, "Bitcoin", R.drawable.icon_chain_bitcoin_512,R.drawable.icon_chain_black_btc_32, R.drawable.home_btc_chain_w,0xffDDA800),
    POLYGON("polygon", "", "Polygon", "MATIC", 18, 60, "Polygon", R.drawable.icon_chain_polygon_512,R.drawable.icon_chain_black_polygon_32, R.drawable.home_polygon_chain_w,0xff333333),
    TRON("tron", "", "Tron", "TRX", 6, 195, "Tron", R.drawable.icon_chain_tron_512,R.drawable.icon_chain_black_tron_32, R.drawable.home_tron_chain_w,0xff333333);

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
        if (this == BITCOIN) {
             return BitcoinUtil.getLocalBitcoinDerivatinPath();
        } else {
            return "44H/" + coinType + "H/0H/0/%s";
        }

    }

    public String getDerivationPath(int index) {
        if (this == BITCOIN) {
            return BitcoinUtil.getLocalBitcoinDerivatinPath(index);
        } else {
            return String.format("44H/%sH/0H/0/%s", coinType, index);
        }

    }


    public String getNodeUrl() {
        return FunctionxNodeConfig.getInstance().getNodeConfig(this).getUrl();
    }


    public static boolean isValidAddress(Coin coin, String input) {
        switch (coin) {
            case ETHEREUM:
            case BINANCE_BSC:
                return isValidEthAddress(input);
            case BITCOIN:
                return isValidBtcAddress(input);
            case FX_COIN: {
                try {
                    final Bech32.Bech32Data decode = Bech32.decode(input);
                    final String hrp = decode.hrp;
                    if (hrp.equals("fx")) {
                        return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
            case FX_PAYMENT: {
                try {
                    final Bech32.Bech32Data decode = Bech32.decode(input);
                    final String hrp = decode.hrp;
                    if (hrp.equals("pay")) {
                        return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }

            case BINANCE: {
                try {
                    final Bech32.Bech32Data decode = Bech32.decode(input);
                    final String hrp = decode.hrp;
                    if (hrp.equals("bnb") || hrp.equals("tbnb")) {
                        return true;
                    }
                } catch (Exception e) {
                }
                return false;
            }
            default:
                return false;
        }
    }

    private static boolean isValidBtcAddress(String input) {
        Address address = null;
        try {
            address = Address.fromString(MainNetParams.get(), input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            address = Address.fromString(TestNet3Params.get(), input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (address != null) {
            return true;
        }
        return false;
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
