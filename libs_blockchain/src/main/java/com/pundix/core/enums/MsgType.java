package com.pundix.core.enums;

import android.text.TextUtils;

import cosmos.bank.v1beta1.Tx;
import fx.gravity.v1.Msgs;

public enum MsgType {
    TRANSFER("Transfer", "", ""),
    TRANSFER1("Transfer", "0xa9059cbb", ""),
    TRANSFER_NFT("Transfer", "0x23b872dd", ""),
    APPROVE("Approve", "0x095ea7b3", ""),
    REWAED("Get Reward", "0x3d18b912", ""),
    WITHDRAW2("Withdraw", "0x69328dec", ""),
    WITHDRAW3("Withdraw", "0x2e1a7d4d", ""),
    WITHDRAW4("Withdraw", "0xf3fef3a3", ""),
    STAKE("Stake", "0xa694fc3a", ""),
    STAKE2("Stake", "0xadc9772e", ""),
    SWAP("Swap", "0x94b918de", ""),
    DEPOSIT("Deposit", "0xe8eda9df", ""),
    DEPOSIT_ETH("Deposit ETH", "0x58c22be7", ""),
    WITHDRAW_ETH("Withdraw ETH", "0x36118b52", ""),
    TRANSFER_AND_CALL("Transfer And Call", "0x4000aea0", ""),
    SELL_TO_UNI_SWAP("Sell To Uniswap", "0xd9627aa4", ""),
    CROSS_CHAIN_TO_FX("Send To Fx", "0x6189d107", ""),
    CROSS_CHAIN_SUBMIT_BATCH("Submit Batch", "0x332caa1f", ""),
    CROSS_CHAIN_DEPOSIT("deposit", "0x7dcc9f07", ""),
    CLAIM("Claim", "0x379607f5", ""),
    MSG_UNDELEGATE("Undelegate", cosmos.staking.v1beta1.Tx.MsgUndelegate.getDescriptor().getName(), cosmos.staking.v1beta1.Tx.MsgUndelegate.getDescriptor().getFullName()),
    MSG_WITHDRAW_DELEGATION_REWARD("WithdrawDelegatorReward", cosmos.distribution.v1beta1.Tx.MsgWithdrawDelegatorReward.getDescriptor().getName(), cosmos.distribution.v1beta1.Tx.MsgWithdrawDelegatorReward.getDescriptor().getFullName()),
    MSG_DELEGATE("Delegate", cosmos.staking.v1beta1.Tx.MsgDelegate.getDescriptor().getName(), cosmos.staking.v1beta1.Tx.MsgDelegate.getDescriptor().getFullName()),
    MSG_BEGIN_REDELEAGTE("Redelegate", cosmos.staking.v1beta1.Tx.MsgBeginRedelegate.getDescriptor().getName(), cosmos.staking.v1beta1.Tx.MsgBeginRedelegate.getDescriptor().getFullName()),
    MSG_SNED_TO_ETH("Send To Eth", Msgs.MsgSendToEth.getDescriptor().getName(), Msgs.MsgSendToEth.getDescriptor().getFullName()),
    MSG_CREAT_ORDER("MsgCreateOrder", fx.dex.Tx.MsgCreateOrder.getDescriptor().getName(), fx.dex.Tx.MsgCreateOrder.getDescriptor().getFullName()),
    MSG_CANCEL_ORDER("MsgCancelOrder", fx.dex.Tx.MsgCancelOrder.getDescriptor().getName(), fx.dex.Tx.MsgCancelOrder.getDescriptor().getFullName()),
    MSG_ADD_MARGIN("MsgAddMargin", fx.dex.Tx.MsgAddMargin.getDescriptor().getName(), fx.dex.Tx.MsgAddMargin.getDescriptor().getFullName()),
    MSG_CLOSE_POSITION("MsgClosePosition", fx.dex.Tx.MsgClosePosition.getDescriptor().getName(), fx.dex.Tx.MsgClosePosition.getDescriptor().getFullName()),
    MSG_SEND_TO_EXTERNAL("MsgSendToExternal", fx.gravity.crosschain.v1.Tx.MsgSendToExternal.getDescriptor().getName(), fx.gravity.crosschain.v1.Tx.MsgSendToExternal.getDescriptor().getFullName()),
    MSG_SUBMIT_PROPOSAL("MsgSubmitProposal", cosmos.gov.v1beta1.Tx.MsgSubmitProposal.getDescriptor().getName(), cosmos.gov.v1beta1.Tx.MsgSubmitProposal.getDescriptor().getFullName()),
    MSG_VOTE("MsgVote", cosmos.gov.v1beta1.Tx.MsgVote.getDescriptor().getName(), cosmos.gov.v1beta1.Tx.MsgVote.getDescriptor().getFullName()),
    CROSS_CHAIN_TRANSFER_IN("SendToFX", Msgs.MsgDepositClaim.getDescriptor().getName(), Msgs.MsgDepositClaim.getDescriptor().getFullName()),
    MSG_DEPOSIT("MsgDeposit", cosmos.gov.v1beta1.Tx.MsgDeposit.getDescriptor().getName(), cosmos.gov.v1beta1.Tx.MsgDeposit.getDescriptor().getFullName()),
    MSG_TRANSFER(fx.ibc.applications.transfer.v1.Tx.MsgTransfer.getDescriptor().getName(), fx.ibc.applications.transfer.v1.Tx.MsgTransfer.getDescriptor().getName(), fx.ibc.applications.transfer.v1.Tx.MsgTransfer.getDescriptor().getFullName()),
    TRANSFER2("Transfer", Tx.MsgSend.getDescriptor().getName(), Tx.MsgSend.getDescriptor().getFullName()),
    MSG_SEND_TO_FX_CLAIM("MsgSendToFxClaim", fx.gravity.crosschain.v1.Tx.MsgSendToFxClaim.getDescriptor().getName(), fx.gravity.crosschain.v1.Tx.MsgSendToFxClaim.getDescriptor().getFullName()),
    TRANSFER3("Transfer", Protocol.Transaction.Contract.ContractType.TransferContract.name(), Protocol.Transaction.Contract.ContractType.TransferContract.getValueDescriptor().getFullName()),
    TRANSFER4("Transfer", Protocol.Transaction.Contract.ContractType.TransferAssetContract.name(), Protocol.Transaction.Contract.ContractType.TransferAssetContract.getValueDescriptor().getFullName()),
    TRANSFER5("Transfer", Protocol.Transaction.Contract.ContractType.TriggerSmartContract.name(), Protocol.Transaction.Contract.ContractType.TriggerSmartContract.getValueDescriptor().getFullName()),
    UNKNOWN("", "", "");
    String title;
    String path;
    String name;

    MsgType(String title, String name, String path) {
        this.name = name;
        this.path = path;
        this.title = title;
    }


    public boolean isCorssChain() {
        switch (this) {
            case MSG_SNED_TO_ETH:
            case CROSS_CHAIN_TRANSFER_IN:
            case CROSS_CHAIN_TO_FX:
            case CROSS_CHAIN_SUBMIT_BATCH:
            case CROSS_CHAIN_DEPOSIT:
            case MSG_TRANSFER:
            case MSG_SEND_TO_EXTERNAL:
            case MSG_SEND_TO_FX_CLAIM:
                return true;
        }
        return false;
    }


    public static MsgType getMethodId(String methodId) {
        for (MsgType m : MsgType.values()) {
            if (m == TRANSFER) {
                continue;
            }
            if (TextUtils.isEmpty(methodId)) {
                return MsgType.TRANSFER;
            }
            final String[] split = methodId.split("\\.");
            if (split.length > 0) {
                final String fxMethid = split[split.length - 1];
                if (fxMethid.equals(m.getName())) {
                    return m;
                }
            } else {
                if (methodId.contains(m.getName())) {
                    return m;
                }
            }

        }
        return MsgType.UNKNOWN;
    }


    public static MsgType getMethod(String method) {
        for (MsgType m : MsgType.values()) {
            if (m.getTitle().equalsIgnoreCase(method)) {
                return m;
            }
        }
        return MsgType.UNKNOWN;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
