package com.hwtx.form.domain.ds;

import com.hwtx.form.domain.ds.metadata.Column;

public class RunValue {
    private String key;
    private Object value;
    private Column column;

    private boolean placeholder = true;

    public RunValue(){}
    public RunValue(Column column, Object value){
        if(null == column){
            this.key = "none";
        }else{
            this.key = column.getName();
        }
        this.column = column;
        this.value = value;
    }
    public RunValue(String key, Object value){
        this.key = key;
        this.value = value;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }
}
