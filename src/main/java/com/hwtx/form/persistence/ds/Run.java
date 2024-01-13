package com.hwtx.form.persistence.ds;

import com.hwtx.form.persistence.ds.metadata.ACTION;
import com.hwtx.form.persistence.ds.metadata.Column;

import java.util.LinkedHashMap;
import java.util.List;

public interface Run {
    Run setRuntime(DataRuntime runtime);

    void init();

    DriverAdapter adapter();

    Run group(String group);

    Run addValue(RunValue value);

    String getTable();

    String getCatalog();

    String getSchema();

    String getDataSource();

    Run setInsertColumns(List<String> keys);

    Run setInsertColumns(LinkedHashMap<String, Column> columns);

    List<String> getInsertColumns();

    LinkedHashMap<String, Column> getInsertColumns(boolean metadata);

    Run setUpdateColumns(List<String> keys);

    Run setUpdateColumns(LinkedHashMap<String, Column> columns);

    List<String> getUpdateColumns();

    LinkedHashMap<String, Column> getUpdateColumns(boolean metadata);

    String getBaseQuery(boolean placeholder);

    default String getBaseQuery() {
        return getBaseQuery(true);
    }

    String getFinalExists(boolean placeholder);

    default String getFinalExists() {
        return getFinalExists(true);
    }

    String getFinalUpdate(boolean placeholder);

    default String getFinalUpdate() {
        return getFinalUpdate(true);
    }

    /**
     * SQL是否支持换行
     *
     * @return boolean
     */
    default boolean supportBr() {
        return true;
    }

    void supportBr(boolean support);

    List<RunValue> getRunValues();

    List<Object> getValues();

    boolean isValid();

    boolean checkValid();

    void setValid(boolean valid);

    StringBuilder getBuilder();

    void setBuilder(StringBuilder builder);

    //1-DataRow 2-Entity
    int getFrom();

    void setFrom(int from);

    void setFilter(Object filter);

    Object getFilter();

    void setUpdate(Object update);

    Object getUpdate();

    Run setQueryColumns(String... columns);

    Run setQueryColumns(List<String> columns);

    List<String> getQueryColumns();

    List<String> getExcludeColumns();

    Run setExcludeColumns(List<String> excludeColumn);

    Run setExcludeColumns(String... columns);

    void setValue(Object value);

    void setValues(String key, List<Object> values);

    Object getValue();

    void setBatch(int batch);

    int getBatch();

    void setVol(int vol);

    int getVol();

    String action();

    void action(String action);

    String log(ACTION.DML action, boolean placeholder);

}
