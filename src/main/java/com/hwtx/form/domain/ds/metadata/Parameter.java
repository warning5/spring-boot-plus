
package com.hwtx.form.domain.ds.metadata;

import java.io.Serializable;

public class Parameter extends BaseMetadata<Parameter> implements Serializable {
    private boolean input;
    private boolean output;
    protected Integer precision                   ; // 整个字段的长度(包含小数部分)  123.45：precision = 5, scale = 2 对于SQL Server 中 varchar(max)设置成 -1
    protected Integer scale                       ; // 小数部分的长度

    private Object value;
    private ColumnType columnType;
    private Integer type = java.sql.Types.VARCHAR;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.columnType = type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }


    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String toString(){
        return "{value:"+value+", type:"+type+"}";
    }

}
