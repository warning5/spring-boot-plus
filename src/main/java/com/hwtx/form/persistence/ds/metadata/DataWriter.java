package com.hwtx.form.persistence.ds.metadata;
public interface DataWriter {
    /**
     * 写入数据库前类型转换(非基础类型时需要)
     *
     * @param value       value
     * @param placeholder 是否启动占位符
     * @return Object
     */
    Object write(Object value, boolean placeholder);

    /**
     * 支持的类型符合这些类型的 在写入数据库之前 由当前writer转换
     *
     * @return class ColumnType StringColumnType
     */
    default Object[] supports() {
        return null;
    }
}
