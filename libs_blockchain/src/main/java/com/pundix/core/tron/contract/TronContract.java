package com.pundix.core.tron.contract;

import com.pundix.common.utils.ByteUtils;
import com.pundix.core.coin.Coin;

import org.tron.common.utils.AbiUtil;

import java.math.BigInteger;

import io.functionx.acc.AccKey;

public class TronContract {

    public static String checkAssetStatusToAbi(String contractAddress) {
        String transferMethod = "checkAssetStatus(address)";
        String transferParams = "\"" + contractAddress + "\"";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

    public static String allowanceToAbi(String address, String approveContractAddress) {
        String transferMethod = "allowance(address,address)";
        String transferParams = "\"" + address + "\"," + "\"" + approveContractAddress + "\"";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }


    public static String transferToAbi(String address, String amount) {
        String transferParams = "\"" + address + "\"," + amount;
        String contractTrigger = AbiUtil.parseMethod("transfer(address,uint256)", transferParams);
        return contractTrigger;
    }


    public static String balanceOfToAbi(String address) {
        String transferMethod = "balanceOf(address)";
        String transferParams = "\"" + address + "\"";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

    public static String nameOfToAbi() {
        String transferMethod = "name()";
        String transferParams = "";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }


    public static String symbolOfToAbi() {
        String transferMethod = "symbol()";
        String transferParams = "";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

    public static String decimalsOfToAbi() {
        String transferMethod = "decimals()";
        String transferParams = "";
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

    public static String approveToAbi(String _spender, String _value) {
        String transferMethod = "approve(address,uint256)";
        String transferParams = "\"" + _spender + "\"," + _value;
        String contractTrigger = "";
        contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

    public static String sendToFxAbi(String _tokenContract, String _destination, Coin toCoin, BigInteger _amount) {
        final byte[] decodeAddress = AccKey.decodeAddress(_destination);
        StringBuffer appendStr = new StringBuffer();
        for (int i = 0; i < (32 - decodeAddress.length); i++) {
            appendStr.append("00");
        }
        byte[] zeroByte = ByteUtils.hexStringToByteArray(appendStr.toString());
        final byte[] byteAddress = ByteUtils.mergeByte(zeroByte, decodeAddress);
        String chain = "";
        if (toCoin == Coin.FX_COIN) {
            chain = "0000000000000000000000000000000000000000000000000000000000000000";
        } else if (toCoin == Coin.FX_PUNDIX) {
            chain = ByteUtils.toHexString(ByteUtils.strToByte32("px/transfer/channel-0"));
        }
        String transferMethod = "sendToFx(address,bytes32,bytes32,uint256)";
        String transferParams = "\"" + _tokenContract + "\",\"" + ByteUtils.toHexString(byteAddress) + "\",\"" + chain + "\"," + _amount.toString();
        String contractTrigger = AbiUtil.parseMethod(transferMethod, transferParams);
        return contractTrigger;
    }

}
