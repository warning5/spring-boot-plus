package com.hwtx.form.domain.ds.metadata;

import lombok.Getter;

public class SrcKeyAdapter implements KeyAdapter {
    @Getter
    private static KeyAdapter instance = new SrcKeyAdapter();

    @Override
    public String key(String key) {
        return key;
    }
    @Override
    public KEY_CASE getKeyCase() {
        return KEY_CASE.SRC;
    }
}
