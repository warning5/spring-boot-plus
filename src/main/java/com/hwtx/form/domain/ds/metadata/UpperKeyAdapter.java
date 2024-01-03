package com.hwtx.form.domain.ds.metadata;


import lombok.Getter;

public class UpperKeyAdapter implements KeyAdapter {
    @Getter
    private static KeyAdapter instance = new UpperKeyAdapter();

    @Override
    public String key(String key) {
        if (null != key) {
            return key.toUpperCase();
        }
        return null;
    }

    @Override
    public KEY_CASE getKeyCase() {
        return KEY_CASE.UPPER;
    }
}
