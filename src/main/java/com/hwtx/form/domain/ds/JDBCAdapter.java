package com.hwtx.form.domain.ds;

import com.hwtx.form.domain.ds.metadata.Column;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

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
