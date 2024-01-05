package com.hwtx.form.domain.ds;

import com.hwtx.form.domain.ds.metadata.BeanUtil;
import com.hwtx.form.domain.ds.metadata.Column;
import com.hwtx.form.domain.ds.metadata.ColumnType;
import com.hwtx.form.util.BasicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public abstract class BasicRun implements Run {
    protected static final Logger log = LoggerFactory.getLogger(BasicRun.class);
    protected StringBuilder builder = new StringBuilder();
    protected int batch;
    protected int vol;//每行多少个值
    protected String catalog;
    protected String schema;
    protected String table;
    protected List<String> keys;
    protected List<RunValue> values;
    protected List<RunValue> batchValues;
    protected String having;

    protected Object filter;
    protected Object update;
    protected Object value;

    protected boolean valid = true;
    protected LinkedHashMap<String, Column> insertColumns = null;
    protected LinkedHashMap<String, Column> updateColumns;
    protected List<String> queryColumns;    //查询列
    protected List<String> excludeColumn;  //不查询列
    protected int from = 1;
    protected boolean supportBr = true;

    protected DataRuntime runtime;
    protected String delimiterFr;
    protected String delimiterTo;

    protected String action;

    public DriverAdapter adapter() {
        if (null != runtime) {
            return runtime.getAdapter();
        }
        return null;
    }

    @Override
    public Run setRuntime(DataRuntime runtime) {
        this.runtime = runtime;
        return this;
    }

    @Override
    public int getFrom() {
        return from;
    }

    @Override
    public void setFrom(int from) {
        this.from = from;
    }

    @Override
    public void init() {
        if (null != runtime) {
            this.delimiterFr = runtime.getAdapter().getDelimiterFr();
            this.delimiterTo = runtime.getAdapter().getDelimiterTo();
        }
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String getDataSource() {
        String ds = table;
        if (BasicUtil.isNotEmpty(ds) && BasicUtil.isNotEmpty(schema)) {
            ds = schema + "." + ds;
        }
        if (BasicUtil.isEmpty(ds)) {
            ds = schema;
        }
        return ds;
    }

    @Override
    public Run group(String group) {
        /*避免添加空条件*/
        if (BasicUtil.isEmpty(group)) {
            return this;
        }
        return this;
    }

    @Override
    public List<RunValue> getRunValues() {
        if (null != batchValues) {
            return batchValues;
        }
        return values;
    }

    @Override
    public List<Object> getValues() {
        List<Object> list = new ArrayList<>();
        if (null != batchValues) {
            for (RunValue value : batchValues) {
                list.add(value.getValue());
            }
        } else if (null != values) {
            for (RunValue value : values) {
                list.add(value.getValue());
            }
        }
        return list;
    }

    @Override
    public void setValues(String key, List<Object> values) {
        if (null != values) {
            if (null == this.values) {
                this.values = new ArrayList<>();
            }
            for (Object value : values) {
                this.values.add(new RunValue(key, value));
            }
        }
    }

    /**
     * 添加参数值
     *
     * @param run run
     * @return Run 最终执行命令 如果是JDBC类型库 会包含 SQL 与 参数值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Run addValues(RunValue run) {
        if (null == values) {
            values = new ArrayList<>();
        }
        values.add(run);
        return this;
    }

    public Run addValues(List<RunValue> values) {
        for (RunValue value : values) {
            addValues(value);
        }
        return this;
    }

    public Run setRunValues(List<RunValue> values) {
        this.values = values;
        return this;
    }

    public String getDelimiterFr() {
        return delimiterFr;
    }

    public void setDelimiterFr(String delimiterFr) {
        this.delimiterFr = delimiterFr;
    }

    public String getDelimiterTo() {
        return delimiterTo;
    }

    public void setDelimiterTo(String delimiterTo) {
        this.delimiterTo = delimiterTo;
    }

    public DriverAdapter getAdapter() {
        return runtime.getAdapter();
    }

    @Override
    public String getBaseQuery(boolean placeholder) {
        String text = builder.toString();
        if (!placeholder) {
            text = replace(text);
        }
        return text;
    }

    public Run addValue(RunValue value) {
        if (null == values) {
            values = new ArrayList<>();
        }
        values.add(value);
        return this;
    }

    @Override
    public String getFinalUpdate(boolean placeholder) {
        String text = builder.toString();
        if (!placeholder) {
            text = replace(text);
        }
        return text;
    }

    public boolean supportBr() {
        return supportBr;
    }

    public void supportBr(boolean support) {
        this.supportBr = support;
    }

    @Override
    public boolean isValid() {
        if (!valid) {
            return false;
        }
        valid = checkValid();
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public void setBuilder(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public StringBuilder getBuilder() {
        return this.builder;
    }

    @Override
    public LinkedHashMap<String, Column> getInsertColumns(boolean metadata) {
        return insertColumns;
    }

    @Override
    public List<String> getInsertColumns() {
        List<String> keys = new ArrayList<>();
        if (null != insertColumns) {
            for (Column column : insertColumns.values()) {
                keys.add(column.getName());
            }
        }
        return keys;
    }

    @Override
    public Run setInsertColumns(List<String> columns) {
        if (null != columns) {
            if (null == insertColumns) {
                insertColumns = new LinkedHashMap<>();
            }
            for (String column : columns) {
                insertColumns.put(column.toUpperCase(), new Column(column));
            }
        }
        return this;
    }

    @Override
    public Run setInsertColumns(LinkedHashMap<String, Column> columns) {
        this.insertColumns = columns;
        return this;
    }

    @Override
    public LinkedHashMap<String, Column> getUpdateColumns(boolean metadata) {
        return updateColumns;
    }

    @Override
    public List<String> getUpdateColumns() {
        List<String> keys = new ArrayList<>();
        if (null != updateColumns) {
            for (Column column : updateColumns.values()) {
                keys.add(column.getName());
            }
        }
        return keys;
    }

    @Override
    public Run setUpdateColumns(List<String> columns) {
        if (null != columns) {
            if (null == updateColumns) {
                updateColumns = new LinkedHashMap<>();
            }
            for (String column : columns) {
                updateColumns.put(column.toUpperCase(), new Column(column));
            }
        }
        return this;
    }

    @Override
    public Run setUpdateColumns(LinkedHashMap<String, Column> columns) {
        this.updateColumns = columns;
        return this;
    }
    @Override
    public void setFilter(Object filter) {
        this.filter = filter;
    }

    @Override
    public Object getFilter() {
        return filter;
    }

    @Override
    public Object getUpdate() {
        return update;
    }

    @Override
    public void setUpdate(Object update) {
        this.update = update;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
/*

	@Override
	public void setValues(List<Object> values) {
		this.batchValues = new ArrayList<>();
	}
*/


    @Override
    public Run setQueryColumns(String... columns) {
        if (null != columns) {
            this.queryColumns = BeanUtil.array2list(columns);
        }
        return this;
    }

    @Override
    public Run setQueryColumns(List<String> columns) {
        this.queryColumns = columns;
        return this;
    }

    @Override
    public List<String> getQueryColumns() {
        return this.queryColumns;
    }

    @Override
    public List<String> getExcludeColumns() {
        return excludeColumn;
    }

    @Override
    public Run setExcludeColumns(List<String> excludeColumn) {
        this.excludeColumn = excludeColumn;
        return this;
    }

    @Override
    public Run setExcludeColumns(String... columns) {
        if (null != columns) {
            this.queryColumns = BeanUtil.array2list(columns);
        }
        return this;
    }

    @Override
    public int getBatch() {
        return batch;
    }

    @Override
    public void setBatch(int batch) {
        this.batch = batch;
    }

    @Override
    public int getVol() {
        return vol;
    }

    @Override
    public String action() {
        return action;
    }

    @Override
    public void action(String action) {
        this.action = action;
    }

    @Override
    public void setVol(int vol) {
        this.vol = vol;
    }

    /**
     * 替换占位符
     *
     * @param sql sql
     * @return String
     */
    protected String replace(String sql) {
        String result = sql;
        if (null != values) {
            for (RunValue rv : values) {
                Object value = rv.getValue();
                Column column = rv.getColumn();
                ColumnType columnType = null;
                if (null != column) {
                    columnType = column.getColumnType();
                }
                int index = result.indexOf("?");
                String replacement = null;
                if (null == value) {
                    value = "null";
                }
                DriverAdapter adapter = adapter();
                if (null != adapter) {
                    replacement = adapter.write(runtime, column, value, false) + "";
                } else {
                    if (BasicUtil.isNumber(value) || "NULL".equalsIgnoreCase(value.toString())) {
                        replacement = value.toString();
                    } else {
                        replacement = "'" + value + "'";
                    }
                }
                result = result.substring(0, index) + replacement + result.substring(index + 1);

            }
        }
        return result;
    }
}
 
 
