package com.pundix.core.hub;

public class FxBalanceModel {

    private String type;
    private FxValueModel value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FxValueModel getValue() {
        return value;
    }

    public void setValue(FxValueModel value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FxBalanceModel{" +  "type='" + type + '\'' + ", value=" + value + '}';
    }
}