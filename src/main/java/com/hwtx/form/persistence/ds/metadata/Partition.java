package com.hwtx.form.persistence.ds.metadata;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Partition implements Serializable {
    public enum TYPE {LIST, RANGE, HASH}

    //RANGE
    private Object from;
    private Object to;
    //LIST
    private List<Object> list;
    //HASH
    private int modulus;
    private int remainder;

    private TYPE type;
    private Map<String, Column> columns;

    public Partition() {

    }

    public Partition(TYPE type) {
        this.type = type;
    }

    public Partition(TYPE type, String... columns) {
        this.type = type;
        this.columns = new LinkedHashMap<>();
        for (String column : columns) {
            this.columns.put(column.toUpperCase(), new Column(column));
        }
    }

    public Partition setType(TYPE type) {
        this.type = type;
        return this;
    }

    public Partition setColumns(LinkedHashMap<String, Column> columns) {
        this.columns = columns;
        return this;
    }

    public Partition setColumns(String... columns) {
        this.columns = new LinkedHashMap<>();
        for (String column : columns) {
            this.columns.put(column.toUpperCase(), new Column(column));
        }
        return this;
    }

    public Partition addColumn(Column column) {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        columns.put(column.getName().toUpperCase(), column);
        return this;
    }

    public Partition addColumn(String column) {
        return addColumn(new Column(column));
    }

    public Partition setRange(Object from, Object to) {
        this.from = from;
        this.to = to;
        return this;
    }

    public Partition setFrom(Object from) {
        this.from = from;
        return this;
    }

    public Partition setTo(Object to) {
        this.to = to;
        return this;
    }

    public Partition setList(List<Object> list) {
        this.list = list;
        return this;
    }

    public Partition addList(Object... items) {
        if (null == list) {
            this.list = new ArrayList<>();
        }
        for (Object item : items) {
            if (item instanceof Collection) {
                Collection cons = (Collection) item;
                for (Object con : cons) {
                    addList(con);
                }
            } else if (item instanceof Object[]) {
                Object[] objs = (Object[]) item;
                for (Object obj : objs) {
                    addList(obj);
                }
            } else {
                list.add(item);
            }
        }
        return this;
    }

    public Partition setModulus(int modulus) {
        this.modulus = modulus;
        return this;
    }

    public Partition setHash(int modulus, int remainder) {
        this.modulus = modulus;
        this.remainder = remainder;
        return this;
    }

    public Partition setRemainder(int remainder) {
        this.remainder = remainder;
        return this;
    }
}
