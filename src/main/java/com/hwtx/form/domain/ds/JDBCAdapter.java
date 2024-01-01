package com.hwtx.form.domain.ds;

import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;

public interface JDBCAdapter extends DriverAdapter {


    /**
     * column[结果集封装-子流程](方法2)<br/>
     * 方法(2)表头内部遍历
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  column
     * @param rsm     ResultSetMetaData
     * @param index   第几列
     * @return Column
     */
    Column column(DataRuntime runtime, Column column, ResultSetMetaData rsm, int index);

    /**
     * column[结果集封装](方法3)<br/>
     * 有表名的情况下可用<br/>
     * 根据jdbc.datasource.connection.DatabaseMetaData获取指定表的列数据
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param columns columns
     * @param dbmd    DatabaseMetaData
     * @param table   表
     * @param pattern 列名称通配符
     * @param <T>     Column
     * @return LinkedHashMap
     * @throws Exception 异常
     */
    <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, DatabaseMetaData dbmd, Table table, String pattern) throws Exception;


    /**
     * column[结果集封装-子流程](方法3)<br/>
     * 方法(3)内部遍历
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  column
     * @param rs      ResultSet
     * @return Column
     */
    Column column(DataRuntime runtime, Column column, ResultSet rs);


    /**
     * column[结果集封装](方法4)<br/>
     * 解析查询结果metadata(0=1)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param columns columns
     * @param table   表
     * @param set     SqlRowSet由spring封装过的结果集ResultSet
     * @param <T>     Column
     * @return LinkedHashMap
     * @throws Exception
     */
    <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, Table table, SqlRowSet set) throws Exception;

    /**
     * column[结果集封装-子流程](方法4)<br/>
     * 内部遍历<br/>
     * columns(DataRuntime runtime, boolean create, LinkedHashMap columns, Table table, SqlRowSet set)遍历内部<br/>
     * 根据SqlRowSetMetaData获取列属性 jdbc.queryForRowSet(where 1=0)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  获取的数据赋值给column如果为空则新创建一个
     * @param rsm     通过spring封装过的SqlRowSet获取的SqlRowSetMetaData
     * @param index   第几列
     * @return Column
     */
    Column column(DataRuntime runtime, Column column, SqlRowSetMetaData rsm, int index);
}
