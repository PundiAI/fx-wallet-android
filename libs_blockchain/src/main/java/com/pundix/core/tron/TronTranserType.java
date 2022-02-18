package com.pundix.core.tron;

public enum TronTranserType {
    TRANSFER(0),
    TRANSFER_TRC10(1),
    TRANSFER_CONTRACT(2),
    TRANSFER_FREEZE(0);

    int type;
    TronTranserType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
