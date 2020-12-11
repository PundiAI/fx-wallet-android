package com.pundix.core.type;

public enum BlockType {

    HASH("tx"),
    BLOCK("block"),
    ADDRESS("account"),
    VALIDATOR("validator"),
    ETH_ADDRESS("address");
    private String type;

    BlockType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
