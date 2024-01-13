package com.hwtx.form.persistence.ds.metadata;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class PrimaryKey extends Index implements Serializable {
    public PrimaryKey() {
        primary = true;
    }

    public boolean isPrimary() {
        return true;
    }

    public Index addColumn(Column column) {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        column.setNullable(false);
        columns.put(column.getName().toUpperCase(), column);
        return this;
    }
}
