package com.hwtx.form.domain.ds.metadata;

import com.hwtx.form.util.BeanUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class Index extends BaseMetadata<Index> implements Serializable {
    protected String type;
    protected Map<String, Column> columns = new LinkedHashMap<>();
    protected boolean primary; // 是否是主键
    protected boolean cluster; // 是否聚簇索引
    protected boolean fulltext;
    protected boolean spatial;

    protected boolean unique;

    public Index() {
    }

    public Index(String name) {
        setName(name);
    }

    public Index(Table table, String name, boolean unique) {
        setTable(table);
        setName(name);
        setUnique(unique);
    }

    public Index(Table table, String name) {
        setTable(table);
        setName(name);
    }

    public boolean isCluster() {
        if (getmap && null != update) {
            return update.cluster;
        }
        return cluster;
    }

    public Index addColumn(Column column) {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        columns.put(column.getName().toUpperCase(), column);
        return this;
    }

    public Index addColumn(String column) {
        return addColumn(new Column(column));
    }

    public Index addColumn(String column, String order) {
        return addColumn(new Column(column).setOrder(order));
    }

    public Index addColumn(String column, String order, int position) {
        return addColumn(new Column(column).setOrder(order).setPosition(position));
    }

    public String getName() {
        if (null == name) {
            name = "index_";
            if (null != columns) {
                name += BeanUtil.concat(columns.keySet(), "_");
            }
        }
        return name;
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

    public Index setCluster(boolean cluster) {
        if (setmap && null != update) {
            update.setCluster(cluster);
            return this;
        }
        this.cluster = cluster;
        return this;
    }

    public boolean isFulltext() {
        if (getmap && null != update) {
            return update.fulltext;
        }
        return fulltext;
    }

    public Index setFulltext(boolean fulltext) {
        if (setmap && null != update) {
            update.setFulltext(fulltext);
            return this;
        }
        this.fulltext = fulltext;
        return this;
    }

    public boolean isSpatial() {
        if (getmap && null != update) {
            return update.spatial;
        }
        return spatial;
    }

    public Index setSpatial(boolean spatial) {
        if (setmap && null != update) {
            update.setSpatial(spatial);
            return this;
        }
        this.spatial = spatial;
        return this;
    }

    public boolean isPrimary() {
        if (getmap && null != update) {
            return update.primary;
        }
        return primary;
    }

    public Index setPrimary(boolean primary) {
        if (setmap && null != update) {
            update.setPrimary(primary);
            return this;
        }
        this.primary = primary;
        if (primary) {
            setCluster(true);
            setUnique(true);
        }
        return this;
    }
}