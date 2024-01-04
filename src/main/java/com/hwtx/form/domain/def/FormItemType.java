package com.hwtx.form.domain.def;

import lombok.Getter;

@Getter
public enum FormItemType {
    INPUT_NUMBER("input-number"),
    INPUT_TEXT("input-text");

    final String type;
    FormItemType(String type) {
        this.type = type;
    }
}
