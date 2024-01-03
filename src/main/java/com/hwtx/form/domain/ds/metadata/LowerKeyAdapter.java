package com.hwtx.form.domain.ds.metadata;

import lombok.Getter;

public class LowerKeyAdapter implements KeyAdapter {
    @Getter
    private static KeyAdapter instance = new LowerKeyAdapter();
    @Override
    public String key(String key) {
        if(null != key){
            return key.toLowerCase();
        }
        return null;
    }
    @Override
    public KEY_CASE getKeyCase() {
        return KEY_CASE.LOWER;
    }


}
