package com.hwtx.form.persistence.ds.metadata;

import com.hwtx.form.util.BeanUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Constraint extends BaseMetadata<Constraint> implements Serializable {
    public enum TYPE {
        PRIMARY_KEY, UNIQUE, NOT_NULL, FOREIGN_KEY, DEFAULT
    }

    protected TYPE type;
    protected LinkedHashMap<String, Column> columns = new LinkedHashMap<>();

    public Constraint() {
    }

    public Constraint(String name) {
        setName(name);
    }

    public Constraint(Table table, String name) {
        setTable(table);
        setName(name);
    }

    public Constraint(Table table, String name, String type) {
        setTable(table);
        setName(name);
        setType(type);
    }

    public String getName() {
        if (null == name) {
            name = "constraint_";
            if (null != columns) {
                name += BeanUtil.concat(columns.keySet());
            }
        }
        return name;
    }

    public String getTableName(boolean update) {
        Table table = getTable(update);
        if (null != table) {
            return table.getName();
        }
        return null;
    }

    public Table getTable(boolean update) {
        if (update) {
            if (null != table && null != table.getUpdate()) {
                return table.getUpdate();
            }
        }
        return table;
    }


    public boolean isUnique() {
        if (getmap && null != update) {
            return update.isUnique();
        }
        return type == TYPE.UNIQUE || type == TYPE.PRIMARY_KEY;
    }

    public TYPE getType() {
        if (getmap && null != update) {
            return update.type;
        }
        return type;
    }

    public Constraint setType(TYPE type) {
        this.type = type;
        return this;
    }

    public Constraint setType(String type) {
        if (null != type) {
            type = type.toUpperCase();
            if (type.contains("PRIMARY")) {
                this.type = TYPE.PRIMARY_KEY;
            } else if (type.contains("FOREIGN")) {
                this.type = TYPE.FOREIGN_KEY;
            } else if (type.contains("UNIQUE")) {
                this.type = TYPE.UNIQUE;
            } else if (type.contains("NOT")) {
                this.type = TYPE.NOT_NULL;
            }
        }
        return this;
    }

    public LinkedHashMap<String, Column> getColumns() {
        if (getmap && null != update) {
            return update.columns;
        }
        return columns;
    }

    public Column getColumn(String name) {
        if (getmap && null != update) {
            return update.getColumn(name);
        }
        if (null != columns && null != name) {
            return columns.get(name.toUpperCase());
        }
        return null;
    }

    public Constraint setColumns(LinkedHashMap<String, Column> columns) {
        this.columns = columns;
        return this;
    }

    public Constraint addColumn(Column column) {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        columns.put(column.getName().toUpperCase(), column);
        return this;
    }

    public Constraint addColumn(String column) {
        return addColumn(new Column(column));
    }

    public Constraint addColumn(String column, String order) {
        return addColumn(new Column(column).setOrder(order));
    }

    public Constraint addColumn(String column, String order, int position) {
        return addColumn(new Column(column).setOrder(order).setPosition(position));
    }


    public Constraint clone() {
        Constraint copy = super.clone();

        LinkedHashMap<String, Column> cols = new LinkedHashMap<>();
        for (Column column : this.columns.values()) {
            Column col = column.clone();
            cols.put(col.getName().toUpperCase(), col);
        }
        copy.columns = cols;
        return copy;
    }
}
