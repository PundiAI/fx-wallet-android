package com.pundix.core.factory;

import java.io.Serializable;

/**
 * Description：
 *
 * @author Joker
 * @date 2020/5/25
 */
public class TransationResult implements Serializable {
    private static final long serialVersionUID = -468388638631166335L;
    private Integer code;
    private String hash;
    private String msg;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
