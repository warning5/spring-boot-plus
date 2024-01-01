package com.hwtx.form.domain.ds;


import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.TableType;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.exception.SQLUpdateException;
import org.anyline.metadata.*;
import org.anyline.metadata.type.ColumnType;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.proxy.EntityAdapterProxy;
import org.anyline.util.BasicUtil;
import org.anyline.util.BeanUtil;
import org.anyline.util.ConfigTable;
import org.anyline.util.LogUtil;
import org.anyline.util.encrypt.MD5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.*;


/**
 * SQL生成 子类主要实现与分页相关的SQL 以及delimiter
 */
public abstract class DefaultJDBCAdapter extends DefaultDriverAdapter implements JDBCAdapter {
    protected static final Logger log = LoggerFactory.getLogger(DefaultJDBCAdapter.class);

    @Override
    public boolean exists(DataRuntime runtime, Table table) {
        String schema = null;
        if (table.getSchema() != null) {
            schema = table.getSchema().getName();
        }
        List<String> tables = MetaUtil.getTables(runtime.datasource(), schema, table.getName(), TableType.TABLE);
        return CollectionUtils.isNotEmpty(tables);
    }

    @Override
    public DatabaseType type() {
        return DatabaseType.COMMON;
    }

    protected JdbcTemplate jdbc(DataRuntime runtime) {
        return runtime.getProcessor();
    }

    private void init(Table table, DataRow row) {
        table.setObjectId(row.getLong("OBJECT_ID", (Long) null));
        table.setEngine(row.getString("ENGINE"));
        table.setComment(row.getString("TABLE_COMMENT", "COMMENTS", "COMMENT"));
        table.setDataRows(row.getLong("TABLE_ROWS", (Long) null));
        table.setCollate(row.getString("TABLE_COLLATION"));
        table.setDataLength(row.getLong("DATA_LENGTH", (Long) null));
        table.setDataFree(row.getLong("DATA_FREE", (Long) null));
        table.setIncrement(row.getLong("AUTO_INCREMENT", (Long) null));
        table.setIndexLength(row.getLong("INDEX_LENGTH", (Long) null));
        table.setCreateTime(row.getDate("CREATE_TIME", (Date) null));
        table.setUpdateTime(row.getDate("UPDATE_TIME", (Date) null));
        table.setType(row.getString("TABLE_TYPE"));
        table.setEngine(row.getString("ENGINE"));
    }

    protected void init(Table table, ResultSet set, Map<String, Integer> keys) {
        try {
            table.setType(BasicUtil.evl(string(keys, "TABLE_TYPE", set), table.getType()));
        } catch (Exception e) {
        }
        try {
            table.setComment(BasicUtil.evl(string(keys, "REMARKS", set), table.getComment()));
        } catch (Exception e) {
        }
        try {
            table.setTypeCat(BasicUtil.evl(string(keys, "TYPE_CAT", set), table.getTypeCat()));
        } catch (Exception e) {
        }
        try {
            table.setTypeName(BasicUtil.evl(string(keys, "TYPE_NAME", set), table.getTypeName()));
        } catch (Exception e) {
        }
        try {
            table.setSelfReferencingColumn(BasicUtil.evl(string(keys, "SELF_REFERENCING_COL_NAME", set), table.getSelfReferencingColumn()));
        } catch (Exception e) {
        }
        try {
            table.setRefGeneration(BasicUtil.evl(string(keys, "REF_GENERATION", set), table.getRefGeneration()));
        } catch (Exception e) {
        }

    }

    /**
     * table[结果集封装]<br/>
     * 表备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照buildQueryTableRun返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param catalog catalog
     * @param schema  schema
     * @param tables  上一步查询结果
     * @param set     查询结果集
     * @return tables
     * @throws Exception 异常
     */
    @Override
    public <T extends Table> LinkedHashMap<String, T> comments(DataRuntime runtime, int index, boolean create, Catalog catalog, Schema schema, LinkedHashMap<String, T> tables, DataSet set) throws Exception {
        if (null == tables) {
            tables = new LinkedHashMap<>();
        }
        for (DataRow row : set) {
            String name = row.getString("TABLE_NAME");
            String comment = row.getString("TABLE_COMMENT");
            if (null != name && null != comment) {
                Table table = tables.get(name.toUpperCase());
                if (null != table) {
                    table.setComment(comment);
                }
            }
        }
        return tables;
    }

    /**
     * table[结果集封装]<br/>
     * 表备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照buildQueryTableRun返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param catalog catalog
     * @param schema  schema
     * @param tables  上一步查询结果
     * @param set     查询结果集
     * @return tables
     * @throws Exception 异常
     */
    @Override
    public <T extends Table> List<T> comments(DataRuntime runtime, int index, boolean create, Catalog catalog, Schema schema, List<T> tables, DataSet set) throws Exception {
        if (null == tables) {
            tables = new ArrayList<>();
        }
        for (DataRow row : set) {
            String name = row.getString("TABLE_NAME");
            String comment = row.getString("TABLE_COMMENT");
            if (null == catalog) {
                catalog = new Catalog(row.getString("TABLE_CATALOG"));
            }
            if (null == schema) {
                schema = new Schema(row.getString("TABLE_SCHEMA"));
            }

            boolean contains = true;
            T table = table(tables, catalog, schema, name);
            if (null == table) {
                if (create) {
                    table = (T) new Table(catalog, schema, name);
                    contains = false;
                } else {
                    continue;
                }
            }
            table.setComment(comment);
            if (!contains) {
                tables.add(table);
            }
        }
        return tables;
    }


    /* *****************************************************************************************************************
     * 													column
     * -----------------------------------------------------------------------------------------------------------------
     * [调用入口]
     * <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, String random, boolean greedy, Table table, boolean primary);
     * <T extends Column> List<T> columns(DataRuntime runtime, String random, boolean greedy, Catalog catalog, Schema schema, String table);
     * [命令合成]
     * List<Run> buildQueryColumnRun(DataRuntime runtime, Table table, boolean metadata) throws Exception;
     * [结果集封装]
     * <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, int index, boolean create, Table table, LinkedHashMap<String, T> columns, DataSet set) throws Exception;
     * <T extends Column> List<T> columns(DataRuntime runtime, int index, boolean create, Table table, List<T> columns, DataSet set) throws Exception;
     * <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, Table table, String pattern) throws Exception;
     ******************************************************************************************************************/

    /**
     * column[调用入口]<br/>
     * 查询表结构
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param random  用来标记同一组命令
     * @param greedy  贪婪模式 true:如果不填写catalog或schema则查询全部 false:只在当前catalog和schema中查询
     * @param table   表
     * @param primary 是否检测主键
     * @param <T>     Column
     * @return Column
     */
    @Override
    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, String random, boolean greedy, Table table, boolean primary) {
        if (!greedy) {
            checkSchema(runtime, table);
        }
        Catalog catalog = table.getCatalog();
        Schema schema = table.getSchema();

        LinkedHashMap<String, T> columns = null;
        long fr = System.currentTimeMillis();
        if (null == random) {
            random = random(runtime);
        }
        try {

            int qty_total = 0;
            int qty_dialect = 0; //优先根据系统表查询
            int qty_metadata = 0; //再根据metadata解析
            int qty_jdbc = 0; //根据驱动内置接口补充

            // 1.优先根据系统表查询
            try {
                List<Run> runs = buildQueryColumnRun(runtime, table, false);
                if (null != runs) {
                    int idx = 0;
                    for (Run run : runs) {
//                        DataSet set = select(runtime, random, true, (String) null, new DefaultConfigStore().keyCase(KeyAdapter.KEY_CASE.PUT_UPPER), run);
//                        columns = columns(runtime, idx, true, table, columns, set);
                        idx++;
                    }
                }
                if (null != columns) {
                    qty_dialect = columns.size();
                    qty_total = columns.size();
                }
            } catch (Exception e) {
                if (ConfigTable.IS_PRINT_EXCEPTION_STACK_TRACE) {
                    e.printStackTrace();
                }
                if (primary) {
                    e.printStackTrace();
                }
                if (ConfigTable.IS_LOG_SQL && log.isWarnEnabled()) {
                    log.warn("{}[columns][{}][catalog:{}][schema:{}][table:{}][msg:{}]", random, LogUtil.format("根据系统表查询失败", 33), catalog, schema, table, e.toString());
                }
            }
            // 根据驱动内置接口补充
            // 再根据metadata解析 SELECT * FROM T WHERE 1=0
            if (null == columns || columns.size() == 0) {
                try {
                    List<Run> runs = buildQueryColumnRun(runtime, table, true);
                    if (null != runs) {
                        for (Run run : runs) {
                            //todo
                            String sql = null; //run.getFinalQuery();
                            if (BasicUtil.isNotEmpty(sql)) {
                                SqlRowSet set = jdbc(runtime).queryForRowSet(sql);
                                columns = columns(runtime, true, columns, table, set);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (ConfigTable.IS_PRINT_EXCEPTION_STACK_TRACE) {
                        e.printStackTrace();
                    } else {
                        if (ConfigTable.IS_LOG_SQL && log.isWarnEnabled()) {
                            log.warn("{}[columns][{}][catalog:{}][schema:{}][table:{}][msg:{}]", random, LogUtil.format("根据metadata解析失败", 33), catalog, schema, table, e.toString());
                        }
                    }
                }
                if (null != columns) {
                    qty_metadata = columns.size() - qty_dialect;
                    qty_total = columns.size();
                }
            }
            if (ConfigTable.IS_LOG_SQL && log.isInfoEnabled()) {
                log.info("{}[columns][catalog:{}][schema:{}][table:{}][total:{}][根据metadata解析:{}][根据系统表查询:{}][根据驱动内置接口补充:{}][执行耗时:{}ms]", random, catalog, schema, table, qty_total, qty_metadata, qty_dialect, qty_jdbc, System.currentTimeMillis() - fr);
            }

            // 方法(3)根据根据驱动内置接口补充

            if (null == columns || columns.size() == 0) {
                DataSource ds = null;
                Connection con = null;
                DatabaseMetaData metadata = null;
                try {
                    ds = jdbc(runtime).getDataSource();
                    con = DataSourceUtils.getConnection(ds);
                    metadata = con.getMetaData();
                    columns = columns(runtime, true, columns, metadata, table, null);
                } catch (Exception e) {
                    if (ConfigTable.IS_PRINT_EXCEPTION_STACK_TRACE) {
                        e.printStackTrace();
                    }
                } finally {
                    if (!DataSourceUtils.isConnectionTransactional(con, ds)) {
                        DataSourceUtils.releaseConnection(con, ds);
                    }
                }

                if (null != columns) {
                    qty_total = columns.size();
                    qty_jdbc = columns.size() - qty_metadata - qty_dialect;
                }
            }
            if (ConfigTable.IS_LOG_SQL && log.isInfoEnabled()) {
                log.info("{}[columns][catalog:{}][schema:{}][table:{}][total:{}][根据metadata解析:{}][根据系统表查询:{}][根据根据驱动内置接口补充:{}][执行耗时:{}ms]", random, catalog, schema, table, qty_total, qty_metadata, qty_dialect, qty_jdbc, System.currentTimeMillis() - fr);
            }
            //检测主键
            if (ConfigTable.IS_METADATA_AUTO_CHECK_COLUMN_PRIMARY) {
                if (null != columns || columns.size() > 0) {
                    boolean exists = false;
                    for (Column column : columns.values()) {
                        if (column.isPrimaryKey() != -1) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        PrimaryKey pk = primary(runtime, table);
                        if (null != pk) {
                            LinkedHashMap<String, Column> pks = pk.getColumns();
                            if (null != pks) {
                                for (String k : pks.keySet()) {
                                    Column column = columns.get(k);
                                    if (null != column) {
                                        column.primary(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (ConfigTable.IS_PRINT_EXCEPTION_STACK_TRACE) {
                e.printStackTrace();
            } else {
                log.error("[columns][result:fail][table:{}][msg:{}]", random, table, e.toString());
            }
        }
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        return columns;
    }

    /**
     * column[调用入口]<br/>
     * 查询所有表的列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param random  用来标记同一组命令
     * @param greedy  贪婪模式 true:如果不填写catalog或schema则查询全部 false:只在当前catalog和schema中查询
     * @param catalog catalog
     * @param schema  schema
     * @param table   查询所有表时 输入null
     * @param <T>     Column
     * @return List
     */
    @Override
    public <T extends Column> List<T> columns(DataRuntime runtime, String random, boolean greedy, Catalog catalog, Schema schema, String table) {
        return super.columns(runtime, random, greedy, catalog, schema, table);
    }

    /**
     * column[命令合成]<br/>
     * 查询表上的列
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param table    表
     * @param metadata 是否根据metadata(true:SELECT * FROM T WHERE 1=0,false:查询系统表)
     * @return sqls
     */
    @Override
    public List<Run> buildQueryColumnRun(DataRuntime runtime, Table table, boolean metadata) throws Exception {
        List<Run> runs = new ArrayList<>();
        Catalog catalog = null;
        Schema schema = null;
        String name = null;
        if (null != table) {
            name = table.getName();
            catalog = table.getCatalog();
            schema = table.getSchema();
        }
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (metadata) {
            builder.append("SELECT * FROM ");
            name(runtime, builder, table);
            builder.append(" WHERE 1=0");
        }
        return runs;
    }

    /**
     * column[结果集封装]<br/>
     * 根据查询结果集构造Tag
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照 buildQueryColumnRun返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param table   表
     * @param columns 上一步查询结果
     * @param set     查询结果集
     * @return tags tags
     * @throws Exception 异常
     */
    @Override
    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, int index, boolean create, Table table, LinkedHashMap<String, T> columns, DataSet set) throws Exception {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        for (DataRow row : set) {
            String name = row.getString("COLUMN_NAME", "COLNAME");
            T column = columns.get(name.toUpperCase());
            if (null == column) {
                column = (T) new Column();
            }
            column.setName(name);
            init(column, table, row);
            columns.put(name.toUpperCase(), column);
        }
        return columns;
    }

    @Override
    public <T extends Column> List<T> columns(DataRuntime runtime, int index, boolean create, Table table, List<T> columns, DataSet set) throws Exception {
        if (null == columns) {
            columns = new ArrayList<>();
        }
        for (DataRow row : set) {
            String name = row.getString("COLUMN_NAME", "COLNAME");
            T tmp = (T) new Column();
            tmp.setName(name);
            init(tmp, table, row);
            T column = column(tmp, columns);
            if (null == column) {
                column = (T) new Column();
                column.setName(name);
                init(column, table, row);
                columns.add(column);
            }
        }
        return columns;
    }

    @Override
    public HashMap<String, Column> columns(DataRuntime runtime, Table table, String pattern) throws Exception {
        HashMap<String, Column> columns = new LinkedHashMap<>();
        DataSource ds = null;
        Connection con = null;
        try {
            JdbcTemplate jdbc = jdbc(runtime);
            ds = jdbc.getDataSource();
            con = DataSourceUtils.getConnection(ds);

            String catalog = table.getCatalogName();
            String schema = table.getSchemaName();
            DatabaseMetaData dbmd = con.getMetaData();
            String[] tmp = checkSchema(catalog, schema);
            ResultSet set = dbmd.getColumns(tmp[0], tmp[1], table.getName(), pattern);
            Map<String, Integer> keys = keys(set);
            while (set.next()) {
                String name = set.getString("COLUMN_NAME");
                if (null == name) {
                    continue;
                }
                String columnCatalog = string(keys, "TABLE_CAT", set, null);
                if (null != columnCatalog) {
                    columnCatalog = columnCatalog.trim();
                }
                String columnSchema = string(keys, "TABLE_SCHEM", set, null);
                if (null != columnSchema) {
                    columnSchema = columnSchema.trim();
                }
                if (!BasicUtil.equalsIgnoreCase(catalog, columnCatalog)) {
                    continue;
                }
                if (!BasicUtil.equalsIgnoreCase(schema, columnSchema)) {
                    continue;
                }
                Column column = new Column();
                String remark = string(keys, "REMARKS", set, column.getComment());
                checkSchema(column, catalog, schema);
                column.setComment(remark);
                column.setTable(BasicUtil.evl(string(keys, "TABLE_NAME", set, table.getName()), column.getTableName(true)));
                column.setType(integer(keys, "DATA_TYPE", set, column.getType()));
                column.setType(integer(keys, "SQL_DATA_TYPE", set, column.getType()));
                String jdbcType = string(keys, "TYPE_NAME", set, column.getTypeName());
                if (BasicUtil.isEmpty(column.getTypeName())) {
                    //数据库中 有jdbc是支持的类型 如果数据库中有了就不用jdbc的了
                    column.setTypeName(jdbcType);
                }
                column.setJdbcType(jdbcType);
                column.setPrecision(integer(keys, "COLUMN_SIZE", set, column.getPrecision()));
                column.setScale(integer(keys, "DECIMAL_DIGITS", set, column.getScale()));
                column.nullable(bool(keys, "NULLABLE", set, column.isNullable()));
                column.setDefaultValue(value(keys, "COLUMN_DEF", set, column.getDefaultValue()));
                column.setPosition(integer(keys, "ORDINAL_POSITION", set, column.getPosition()));
                column.autoIncrement(bool(keys, "IS_AUTOINCREMENT", set, column.isAutoIncrement()));
                ColumnType columnType = type(column.getTypeName());
                column.setColumnType(columnType);
                column(runtime, column, set);
                column.setName(name);
                columns.put(name, column);
            }
            // 主键

            ResultSet rs = dbmd.getPrimaryKeys(tmp[0], tmp[1], table.getName());
            while (rs.next()) {
                String name = rs.getString(4);
                Column column = columns.get(name.toUpperCase());
                if (null == column) {
                    continue;
                }
                column.primary(true);
            }
        } catch (Exception e) {

        } finally {
            if (null != con && !DataSourceUtils.isConnectionTransactional(con, ds)) {
                DataSourceUtils.releaseConnection(con, ds);
            }
        }
        return columns;
    }

    @Override
    public Map<String, Index> indexs(DataRuntime runtime, Table table) {
        DataSource ds = null;
        Connection con = null;
        Map<String, Index> indexs = new LinkedHashMap<>();
        JdbcTemplate jdbc = jdbc(runtime);
        try {
            ds = jdbc.getDataSource();
            con = DataSourceUtils.getConnection(ds);
            DatabaseMetaData dbmd = con.getMetaData();
            checkSchema(runtime, table);
            String[] tmp = checkSchema(table.getCatalogName(), table.getSchemaName());
            ResultSet set = dbmd.getIndexInfo(tmp[0], tmp[1], table.getName(), false, false);
            Map<String, Integer> keys = keys(set);
            LinkedHashMap<String, Column> columns = null;
            while (set.next()) {
                String name = string(keys, "INDEX_NAME", set);
                if (null == name) {
                    continue;
                }
                Index index = indexs.get(name.toUpperCase());
                if (null == index) {
                    index = new Index();
                    indexs.put(name.toUpperCase(), index);
                    index.setName(string(keys, "INDEX_NAME", set));
                    //index.setType(integer(keys, "TYPE", set, null));
                    index.setUnique(!bool(keys, "NON_UNIQUE", set, false));
                    String catalog = BasicUtil.evl(string(keys, "TABLE_CATALOG", set), string(keys, "TABLE_CAT", set));
                    String schema = BasicUtil.evl(string(keys, "TABLE_SCHEMA", set), string(keys, "TABLE_SCHEM", set));
                    checkSchema(index, catalog, schema);
                    if (!BasicUtil.equals(table.getCatalogName(), index.getCatalogName()) || !BasicUtil.equals(table.getSchemaName(), index.getSchemaName())) {
                        continue;
                    }
                    index.setTable(string(keys, "TABLE_NAME", set));
                    indexs.put(name.toUpperCase(), index);
                    columns = new LinkedHashMap<>();
                    index.setColumns(columns);
                    if (name.equalsIgnoreCase("PRIMARY")) {
                        index.setCluster(true);
                        index.setPrimary(true);
                    } else if (name.equalsIgnoreCase("PK_" + table.getName())) {
                        index.setCluster(true);
                        index.setPrimary(true);
                    }
                } else {
                    columns = index.getColumns();
                }

                String columnName = string(keys, "COLUMN_NAME", set);
                Column column = new Column();
                column.setName(columnName);
                String order = string(keys, "ASC_OR_DESC", set);
                if (null != order && order.startsWith("D")) {
                    order = "DESC";
                } else {
                    order = "ASC";
                }
                column.setOrder(order);
                column.setPosition(integer(keys, "ORDINAL_POSITION", set, null));
                columns.put(column.getName().toUpperCase(), column);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != con && !DataSourceUtils.isConnectionTransactional(con, ds)) {
                DataSourceUtils.releaseConnection(con, ds);
            }
        }
        return indexs;
    }

    @Override
    public long update(DataRuntime runtime, Run run) {
        long result;
        String sql = run.getFinalUpdate();
        if (BasicUtil.isEmpty(sql)) {
            log.warn("无法获取待执行sql");
            return -1;
        }
        List<Object> values = run.getValues();
        int batch = run.getBatch();
        long fr = System.currentTimeMillis();
        try {
            JdbcTemplate jdbc = jdbc(runtime);
            if (batch > 1) {
                result = batch(jdbc, sql, batch, run.getVol(), values);
            } else {
                result = jdbc.update(sql, values.toArray());
            }
            log.info("执行sql - {},spend = {}ms", sql, System.currentTimeMillis() - fr);
        } catch (Exception e) {
            SQLUpdateException ex = new SQLUpdateException("update异常:" + e.toString(), e);
            ex.setSql(sql);
            ex.setValues(values);
            throw ex;
        }
        return result;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 部分数据库在创建主表时用主表关键字(默认)，部分数据库普通表主表子表都用table，部分数据库用collection、timeseries等
     *
     * @param meta 表
     * @return String
     */
    @Override
    public String keyword(Table meta) {
        return meta.getKeyword();
    }

    /**
     * table[命令合成]<br/>
     * 创建表<br/>
     * 关于创建主键的几个环节<br/>
     * 1.1.定义列时 标识 primary(DataRuntime runtime, StringBuilder builder, Column column)<br/>
     * 1.2.定义表时 标识 primary(DataRuntime runtime, StringBuilder builder, Table table)<br/>
     * 1.3.定义完表DDL后，单独创建 primary(DataRuntime runtime, PrimaryKey primary)根据三选一情况调用buildCreateRun<br/>
     * 2.单独创建 buildCreateRun(DataRuntime runtime, PrimaryKey primary)<br/>
     * 其中1.x三选一 不要重复
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return runs
     * @throws Exception
     */
    @Override
    public List<Run> buildCreateRun(DataRuntime runtime, Table meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("CREATE TABLE ");
        name(runtime, builder, meta);
        LinkedHashMap<String, Column> columnsMap = meta.getColumns();
        Collection<Column> columns = null;
        LinkedHashMap<String, Column> pks = null;
        if (null != meta.getPrimaryKey()) {
            pks = meta.getPrimaryKey().getColumns();
        }
        builder.append("(\n");
        int idx = 0;
        for (Column column : columnsMap.values()) {
            builder.append("\t");
            if (idx > 0) {
                builder.append(",");
            }
            column.setAction(ACTION.DDL.COLUMN_ADD);
            delimiter(builder, column.getName()).append(" ");
            define(runtime, builder, column).append("\n");
            idx++;
        }
        builder.append("\t");
        if (MapUtils.isNotEmpty(pks)) {
            primary(runtime, builder, meta);
        }
        builder.append(")");

        //CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='备注';
        charset(runtime, builder, meta);

        comment(runtime, builder, meta);

        List<Run> tableComment = buildAppendCommentRun(runtime, meta);
        if (null != tableComment) {
            runs.addAll(tableComment);
        }
        if (null != columns) {
            for (Column column : columns) {
                List<Run> columnComment = buildAppendCommentRun(runtime, column);
                if (null != columnComment) {
                    runs.addAll(columnComment);
                }
            }
        }

        LinkedHashMap<String, Index> indexs = meta.getIndexs();
        if (null != indexs) {
            for (Index index : indexs.values()) {
                //创建表过程已添加过主键，这里不重复添加
                if (!index.isPrimary()) {
                    runs.addAll(buildAddRun(runtime, index));
                }
            }
        }
        return runs;
    }

    /**
     * table[命令合成]<br/>
     * 删除表
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Table meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        Catalog catalog = meta.getCatalog();
        Schema schema = meta.getSchema();
        builder.append("DROP ").append(keyword(meta)).append(" ");
        name(runtime, builder, meta);
        return runs;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 修改备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildChangeCommentRun(DataRuntime runtime, Table meta) throws Exception {
        return super.buildChangeCommentRun(runtime, meta);
    }


    /**
     * table[命令合成-子流程]<br/>
     * 定义表的主键标识,在创建表的DDL结尾部分(注意不要跟列定义中的主键重复)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    表
     * @return StringBuilder
     */
    @Override
    public StringBuilder primary(DataRuntime runtime, StringBuilder builder, Table meta) {
        PrimaryKey primary = meta.getPrimaryKey();
        LinkedHashMap<String, Column> pks = null;
        if (null != primary) {
            pks = primary.getColumns();
        } else {
            pks = meta.primarys();
        }
        if (!pks.isEmpty()) {
            builder.append(",PRIMARY KEY (");
            boolean first = true;
            for (Column pk : pks.values()) {
                if (!first) {
                    builder.append(",");
                }
                delimiter(builder, pk.getName());
                String order = pk.getOrder();
                if (BasicUtil.isNotEmpty(order)) {
                    builder.append(" ").append(order);
                }
                first = false;
            }
            builder.append(")");
        }
        return builder;
    }


    /**
     * column[调用入口]<br/>
     * 删除列,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    @Override
    public boolean drop(DataRuntime runtime, Column meta) throws Exception {
        return super.drop(runtime, meta);
    }

    /**
     * column[调用入口]<br/>
     * 重命名列,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  列
     * @param name    新名称
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    @Override
    public boolean rename(DataRuntime runtime, Column origin, String name) throws Exception {
        return super.rename(runtime, origin, name);
    }


    /**
     * column[命令合成]<br/>
     * 添加列<br/>
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Column meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (!slice) {
            Table table = meta.getTable(true);
            builder.append("ALTER ").append(keyword(table)).append(" ");
            name(runtime, builder, table);
        }
        // Column update = column.getUpdate();
        // if(null == update){
        // 添加列
        //builder.append(" ADD ").append(column.getKeyword()).append(" ");
        addColumnGuide(runtime, builder, meta);
        delimiter(builder, meta.getName()).append(" ");
        define(runtime, builder, meta);
        // }
        runs.addAll(buildAppendCommentRun(runtime, meta));
        return runs;
    }

    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildAddRun(runtime, meta);
    }

    @Override
    public List<Run> buildAlterRun(DataRuntime runtime, Column meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Column update = meta.getUpdate();
        if (null != update) {
            if (null == update.getTable(false)) {
                update.setTable(meta.getTable(false));
            }
            // 修改列名
            String name = meta.getName();
            String uname = update.getName();
            if (!BasicUtil.equalsIgnoreCase(name, uname) && !uname.endsWith("_TMP_UPDATE_TYPE")) {
                runs.addAll(buildRenameRun(runtime, meta));
            }
            // 修改数据类型
            String type = type(runtime, null, meta).toString();
            String utype = type(runtime, null, update).toString();
            boolean exe = false;
            if (!BasicUtil.equalsIgnoreCase(type, utype)) {
                List<Run> list = buildChangeTypeRun(runtime, meta);
                if (null != list) {
                    runs.addAll(list);
                    exe = true;
                }
            } else {
                //数据类型没变但长度变了
                if (meta.getPrecision() != update.getPrecision() || meta.getScale() != update.getScale()) {
                    List<Run> list = buildChangeTypeRun(runtime, meta);
                    if (null != list) {
                        runs.addAll(list);
                        exe = true;
                    }
                }
            }
            // 修改默认值
            Object def = meta.getDefaultValue();
            Object udef = update.getDefaultValue();
            if (!BasicUtil.equalsIgnoreCase(def, udef)) {
                List<Run> defs = buildChangeDefaultRun(runtime, meta);
                if (null != defs) {
                    runs.addAll(defs);
                }
            }
            // 修改非空限制
            int nullable = meta.isNullable();
            int unullable = update.isNullable();
            if (nullable != unullable) {
                List<Run> nulls = buildChangeNullableRun(runtime, meta);
                if (null != nulls) {
                    runs.addAll(nulls);
                }
            }
            // 修改备注
            String comment = meta.getComment();
            String ucomment = update.getComment();
            if (!BasicUtil.equalsIgnoreCase(comment, ucomment)) {
                List<Run> cmts = buildChangeCommentRun(runtime, meta);
                if (null != cmts) {
                    runs.addAll(cmts);
                }
            }
        }
        return runs;
    }

    /**
     * column[命令合成]<br/>
     * 删除列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Column meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (meta instanceof Tag) {
            Tag tag = (Tag) meta;
            return buildDropRun(runtime, tag);
        }
        if (!slice) {
            Table table = meta.getTable(true);
            builder.append("ALTER ").append(keyword(table)).append(" ");
            name(runtime, builder, table);
        }
        dropColumnGuide(runtime, builder, meta);
        delimiter(builder, meta.getName());
        return runs;
    }

    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildDropRun(runtime, meta);
    }

    /**
     * column[命令合成]<br/>
     * 修改列名<br/>
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildRenameRun(DataRuntime runtime, Column meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        Table table = meta.getTable(true);
        builder.append("ALTER ").append(keyword(table)).append(" ");
        name(runtime, builder, table);
        builder.append(" RENAME ").append(meta.getKeyword()).append(" ");
        delimiter(builder, meta.getName());
        builder.append(" ");
        delimiter(builder, meta.getUpdate());
        meta.setName(meta.getUpdate().getName());
        return runs;
    }


    /**
     * column[命令合成-子流程]<br/>
     * 修改数据类型<br/>
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildChangeTypeRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildChangeTypeRun(runtime, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 修改表的关键字
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @return String
     */
    @Override
    public String alterColumnKeyword(DataRuntime runtime) {
        return super.alterColumnKeyword(runtime);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 添加列引导<br/>
     * alter table sso_user [add column] type_code int
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder StringBuilder
     * @param meta    列
     * @return String
     */
    @Override
    public StringBuilder addColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta) {
        builder.append(" ADD ").append(meta.getKeyword()).append(" ");
        return builder;
    }


    /**
     * column[命令合成-子流程]<br/>
     * 删除列引导<br/>
     * alter table sso_user [drop column] type_code
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder StringBuilder
     * @param meta    列
     * @return String
     */
    @Override
    public StringBuilder dropColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta) {
        builder.append(" DROP ").append(meta.getKeyword()).append(" ");
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 修改默认值
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildChangeDefaultRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildChangeDefaultRun(runtime, meta);
    }


    /**
     * column[命令合成-子流程]<br/>
     * 修改非空限制
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildChangeNullableRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildChangeNullableRun(runtime, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 修改备注
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildChangeCommentRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildChangeCommentRun(runtime, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 添加列备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildAppendCommentRun(DataRuntime runtime, Column meta) throws Exception {
        return super.buildAppendCommentRun(runtime, meta);
    }


    /**
     * column[命令合成-子流程]<br/>
     * 取消自增
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildDropAutoIncrement(DataRuntime runtime, Column meta) throws Exception {
        return super.buildDropAutoIncrement(runtime, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 定义列，依次拼接下面几个属性注意不同数据库可能顺序不一样
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder define(DataRuntime runtime, StringBuilder builder, Column meta) {
        // 数据类型
        type(runtime, builder, meta);
        // 编码
        charset(runtime, builder, meta);
        // 默认值
        defaultValue(runtime, builder, meta);
        // 非空
        nullable(runtime, builder, meta);
        //主键
        primary(runtime, builder, meta);
        // 递增
        if (meta.isPrimaryKey() == 1) {
            increment(runtime, builder, meta);
        }
        // 更新行事件
        onupdate(runtime, builder, meta);
        // 备注
        comment(runtime, builder, meta);
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:创建或删除列之前  检测表是否存在
     * IF NOT EXISTS
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param exists  exists
     * @return StringBuilder
     */
    @Override
    public StringBuilder checkColumnExists(DataRuntime runtime, StringBuilder builder, boolean exists) {
        return super.checkColumnExists(runtime, builder, exists);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:数据类型
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder type(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (null == builder) {
            builder = new StringBuilder();
        }
        boolean isIgnorePrecision = false;
        boolean isIgnoreScale = false;
        String typeName = meta.getTypeName();
        ColumnType type = type(typeName);
        if (null != type) {
            if (!type.support()) {
                throw new RuntimeException("数据类型不支持:" + typeName);
            }
            isIgnorePrecision = type.ignorePrecision();
            isIgnoreScale = type.ignoreScale();
            typeName = type.getName();
        } else {
            isIgnorePrecision = isIgnorePrecision(runtime, meta);
            isIgnoreScale = isIgnoreScale(runtime, meta);
        }
        return type(runtime, builder, meta, typeName, isIgnorePrecision, isIgnoreScale);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:数据类型定义
     *
     * @param runtime           运行环境主要包含驱动适配器 数据源或客户端
     * @param builder           builder
     * @param meta              列
     * @param type              数据类型(已经过转换)
     * @param isIgnorePrecision 是否忽略长度
     * @param isIgnoreScale     是否忽略小数
     * @return StringBuilder
     */
    @Override
    public StringBuilder type(DataRuntime runtime, StringBuilder builder, Column meta, String type, boolean isIgnorePrecision, boolean isIgnoreScale) {
        if (null == builder) {
            builder = new StringBuilder();
        }
        builder.append(type);
        if (!isIgnorePrecision) {
            Integer precision = meta.getPrecision();
            if (null != precision) {
                if (precision > 0) {
                    builder.append("(").append(precision);
                    Integer scale = meta.getScale();
                    if (null != scale && scale > 0 && !isIgnoreScale) {
                        builder.append(",").append(scale);
                    }
                    builder.append(")");
                } else if (precision == -1) {
                    builder.append("(max)");
                }
            }
        }
        String child = meta.getChildTypeName();
        Integer srid = meta.getSrid();
        if (null != child) {
            builder.append("(");
            builder.append(child);
            if (null != srid) {
                builder.append(",").append(srid);
            }
            builder.append(")");
        }
        if (meta.isArray()) {
            builder.append("[]");
        }
        return builder;
    }


    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略长度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return boolean
     */
    @Override
    public boolean isIgnorePrecision(DataRuntime runtime, Column meta) {
        ColumnType type = meta.getColumnType();
        if (null != type) {
            return type.ignorePrecision();
        }
        String typeName = meta.getTypeName();
        if (null != typeName) {
            String chk = typeName.toUpperCase();
            Boolean chkResult = checkIgnorePrecision(runtime, chk);
            if (null != chkResult) {
                return chkResult;
            }
        }
        return false;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略精度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return boolean
     */
    @Override
    public boolean isIgnoreScale(DataRuntime runtime, Column meta) {
        ColumnType type = meta.getColumnType();
        if (null != type) {
            return type.ignoreScale();
        }
        String name = meta.getTypeName();
        if (null != name) {
            String chk = name.toUpperCase();
            Boolean chkResult = checkIgnoreScale(runtime, chk);
            if (null != chkResult) {
                return chkResult;
            }
        }
        return false;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略长度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return Boolean 检测不到时返回null
     */
    @Override
    public Boolean checkIgnorePrecision(DataRuntime runtime, String type) {
        return super.checkIgnorePrecision(runtime, type);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略精度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return Boolean 检测不到时返回null
     */
    @Override
    public Boolean checkIgnoreScale(DataRuntime runtime, String type) {
        return super.checkIgnoreScale(runtime, type);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:非空
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder nullable(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (meta.isNullable() == 0) {
            int nullable = meta.isNullable();
            if (nullable != -1) {
                if (nullable == 0) {
                    builder.append(" NOT");
                }
                builder.append(" NULL");
            }
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder charset(DataRuntime runtime, StringBuilder builder, Column meta) {
        return super.charset(runtime, builder, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:默认值
     *
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder defaultValue(DataRuntime runtime, StringBuilder builder, Column meta) {
        Object def;
        if (null != meta.getUpdate()) {
            def = meta.getUpdate().getDefaultValue();
        } else {
            def = meta.getDefaultValue();
        }
        if (null != def) {
            builder.append(" DEFAULT ");
            if (def instanceof DriverAdapter.SQL_BUILD_IN_VALUE) {
                builder.append(def);
            } else {
                def = write(runtime, meta, def, false);
                if (null == def) {
                    def = meta.getDefaultValue();
                }
                builder.append(def);
            }
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:定义列的主键标识(注意不要跟表定义中的主键重复)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder primary(DataRuntime runtime, StringBuilder builder, Column meta) {
        return super.primary(runtime, builder, meta);
    }

    @Override
    public PrimaryKey primary(DataRuntime runtime, Table table) {
        return null;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:递增列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder increment(DataRuntime runtime, StringBuilder builder, Column meta) {
        return super.increment(runtime, builder, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:更新行事件
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder onupdate(DataRuntime runtime, StringBuilder builder, Column meta) {
        return super.onupdate(runtime, builder, meta);
    }

    /**
     * primary[调用入口]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean add(DataRuntime runtime, PrimaryKey meta) throws Exception {
        return super.add(runtime, meta);
    }

    /**
     * primary[调用入口]<br/>
     * 修改主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  原主键
     * @param meta    新主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean alter(DataRuntime runtime, Table table, PrimaryKey origin, PrimaryKey meta) throws Exception {
        return super.alter(runtime, table, origin, meta);
    }

    /**
     * primary[调用入口]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean drop(DataRuntime runtime, PrimaryKey meta) throws Exception {
        return super.drop(runtime, meta);
    }

    /**
     * primary[调用入口]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  主键
     * @param name    新名称
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean rename(DataRuntime runtime, PrimaryKey origin, String name) throws Exception {
        return super.rename(runtime, origin, name);
    }

    /**
     * primary[命令合成]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, PrimaryKey meta, boolean slice) throws Exception {
        return super.buildAddRun(runtime, meta, slice);
    }

    /**
     * primary[命令合成]<br/>
     * 修改主键
     * 有可能生成多条SQL
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  原主键
     * @param meta    新主键
     * @return List
     */
    @Override
    public List<Run> buildAlterRun(DataRuntime runtime, PrimaryKey origin, PrimaryKey meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        if (null != meta) {//没有新主键的就不执行了
            Table table = null;
            if (null != meta) {
                table = meta.getTable();
            } else {
                table = origin.getTable();
            }
            List<Run> slices = new ArrayList<>();
            if (null != origin) {
                slices.addAll(buildDropRun(runtime, origin, true));
            }
            if (null != meta) {
                slices.addAll(buildAddRun(runtime, meta, true));
            }
            if (!slices.isEmpty()) {
                Run run = new SimpleRun(runtime);
                runs.add(run);
                StringBuilder builder = run.getBuilder();
                builder.append("ALTER TABLE ");
                name(runtime, builder, table);
                boolean first = true;
                for (Run item : slices) {
                    if (!first) {
                        builder.append(",");
                    }
                    builder.append(item.getBuilder());
                    first = false;
                }
            }
        }
        return runs;
    }

    /**
     * primary[命令合成]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, PrimaryKey meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("ALTER TABLE ");
        name(runtime, builder, meta.getTable(true));
        builder.append(" DROP CONSTRAINT ");
        delimiter(builder, meta.getName());
        return runs;
    }

    /**
     * primary[命令合成]<br/>
     * 修改主键名
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @return String
     */
    @Override
    public List<Run> buildRenameRun(DataRuntime runtime, PrimaryKey meta) throws Exception {
        return super.buildRenameRun(runtime, meta);
    }

    /* *****************************************************************************************************************
     * 													index
     * -----------------------------------------------------------------------------------------------------------------
     * [调用入口]
     * boolean add(DataRuntime runtime, Index meta)
     * boolean alter(DataRuntime runtime, Index meta)
     * boolean alter(DataRuntime runtime, Table table, Index meta)
     * boolean drop(DataRuntime runtime, Index meta)
     * boolean rename(DataRuntime runtime, Index origin, String name)
     * [命令合成]
     * List<Run> buildAddRun(DataRuntime runtime, Index meta)
     * List<Run> buildAlterRun(DataRuntime runtime, Index meta)
     * List<Run> buildDropRun(DataRuntime runtime, Index meta)
     * List<Run> buildRenameRun(DataRuntime runtime, Index meta)
     * [命令合成-子流程]
     * StringBuilder type(DataRuntime runtime, StringBuilder builder, Index meta)
     * StringBuilder comment(DataRuntime runtime, StringBuilder builder, Index meta)
     ******************************************************************************************************************/

    /**
     * index[调用入口]<br/>
     * 添加索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean add(DataRuntime runtime, Index meta) throws Exception {
        return super.add(runtime, meta);
    }

    /**
     * index[调用入口]<br/>
     * 删除索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean drop(DataRuntime runtime, Index meta) throws Exception {
        return super.drop(runtime, meta);
    }

    /**
     * index[命令合成]<br/>
     * 添加索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Index meta) throws Exception {
        String name = meta.getName();
        if (BasicUtil.isEmpty(name)) {
            name = "index_" + BasicUtil.getRandomString(10);
        }
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("CREATE");
        if (meta.isUnique()) {
            builder.append(" UNIQUE");
        } else if (meta.isFulltext()) {
            builder.append(" FULLTEXT");
        } else if (meta.isSpatial()) {
            builder.append(" SPATIAL");
        }
        builder.append(" INDEX ").append(name)
                .append(" ON ");//.append(index.getTableName(true));
        Table table = meta.getTable(true);
        name(runtime, builder, table);
        builder.append("(");
        int qty = 0;
        Collection<Column> cols = meta.getColumns().values();
        for (Column column : cols) {
            if (qty > 0) {
                builder.append(",");
            }
            delimiter(builder, column.getName());
            String order = column.getOrder();
            if (BasicUtil.isNotEmpty(order)) {
                builder.append(" ").append(order);
            }
            qty++;
        }
        builder.append(")");
        type(runtime, builder, meta);
        comment(runtime, builder, meta);
        return runs;
    }

    /**
     * index[命令合成]<br/>
     * 删除索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return String
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Index meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        Table table = meta.getTable(true);
        if (meta.isPrimary()) {
            builder.append("ALTER TABLE ");
            name(runtime, builder, table);
            builder.append(" DROP CONSTRAINT ").append(meta.getName());
        } else {
            builder.append("DROP INDEX ").append(meta.getName());
            if (BasicUtil.isNotEmpty(table)) {
                builder.append(" ON ");
                name(runtime, builder, table);
            }
        }
        return runs;
    }

    /**
     * column [结果集封装-子流程](方法1)<br/>
     * 方法(1)内部遍历
     *
     * @param column
     * @param table
     * @param row
     */
    protected void init(Column column, Table table, DataRow row) {
        String catalog = BasicUtil.evl(row.getString("TABLE_CATALOG"), column.getCatalogName(), table.getCatalogName());
        if (null != catalog) {
            catalog = catalog.trim();
        }
        String schema = BasicUtil.evl(row.getString("TABLE_SCHEMA", "TABSCHEMA", "SCHEMA_NAME", "OWNER"), column.getSchemaName(), table.getSchemaName());
        if (null != schema) {
            schema = schema.trim();
        }
        checkSchema(column, catalog, schema);
        if (null != table.getName()) {//查询全部表
            column.setTable(table);
        }
        column.setTable(BasicUtil.evl(row.getString("TABLE_NAME", "TABNAME"), table.getName(), column.getTableName(true)));

        if (null == column.getPosition()) {
            try {
                column.setPosition(row.getInt("ORDINAL_POSITION", "COLNO", "POSITION"));
            } catch (Exception e) {
            }
        }
        column.setComment(BasicUtil.evl(row.getString("COLUMN_COMMENT", "COMMENTS", "REMARKS"), column.getComment()));
        String type = row.getString("FULL_TYPE", "DATA_TYPE", "TYPE_NAME", "TYPENAME", "DATA_TYPE_NAME");
		/*if(null != type){
			type = type.replace("character varying","VARCHAR");
		}*/
        //FULL_TYPE pg中pg_catalog.format_type合成的
        //character varying
        //TODO timestamp without time zone
        //TODO 子类型  geometry(Polygon,4326) geometry(Polygon) geography(Polygon,4326)
        if (null != type && type.contains(" ")) {
            type = row.getString("UDT_NAME", "DATA_TYPE", "TYPENAME", "DATA_TYPE_NAME");
        }
        column.setTypeName(BasicUtil.evl(type, column.getTypeName()));
        String def = BasicUtil.evl(row.get("COLUMN_DEFAULT", "DATA_DEFAULT", "DEFAULT", "DEFAULT_VALUE", "DEFAULT_DEFINITION"), column.getDefaultValue()) + "";
        if (BasicUtil.isNotEmpty(def)) {
            while (def.startsWith("(") && def.endsWith(")")) {
                def = def.substring(1, def.length() - 1);
            }
            while (def.startsWith("'") && def.endsWith("'")) {
                def = def.substring(1, def.length() - 1);
            }
            column.setDefaultValue(def);
        }
        //默认值约束
        column.setDefaultConstraint(row.getString("DEFAULT_CONSTRAINT"));
        if (-1 == column.isAutoIncrement()) {
            column.autoIncrement(row.getBoolean("IS_IDENTITY", null));
        }
        if (-1 == column.isAutoIncrement()) {
            column.autoIncrement(row.getBoolean("IS_AUTOINCREMENT", null));
        }
        if (-1 == column.isAutoIncrement()) {
            column.autoIncrement(row.getBoolean("IDENTITY", null));
        }
        if (-1 == column.isAutoIncrement()) {
            if (row.getStringNvl("EXTRA").toLowerCase().contains("auto_increment")) {
                column.autoIncrement(true);
            }
        }

        column.setObjectId(row.getLong("OBJECT_ID", (Long) null));
        //主键
        String column_key = row.getString("COLUMN_KEY");
        if ("PRI".equals(column_key)) {
            column.primary(1);
        }
        if (row.getBoolean("PK", Boolean.FALSE)) {
            column.primary(1);
        }

        //非空
        if (-1 == column.isNullable()) {
            try {
                column.nullable(row.getBoolean("IS_NULLABLE", "NULLABLE", "NULLS"));
            } catch (Exception e) {
            }
        }
        //oracle中decimal(18,9) data_length == 22 DATA_PRECISION=18
        try {
            Integer len = row.getInt("NUMERIC_PRECISION", "PRECISION", "DATA_PRECISION", "");
            if (null == len || len == 0) {
                len = row.getInt("CHARACTER_MAXIMUM_LENGTH", "MAX_LENGTH", "DATA_LENGTH", "LENGTH");
            }
            column.setPrecision(len);
        } catch (Exception e) {
        }
        try {
            if (null == column.getScale()) {
                column.setScale(row.getInt("NUMERIC_SCALE", "SCALE", "DATA_SCALE"));
            }
        } catch (Exception e) {
        }
        if (null == column.getCharset()) {
            column.setCharset(row.getString("CHARACTER_SET_NAME"));
        }
        if (null == column.getCollate()) {
            column.setCollate(row.getString("COLLATION_NAME"));
        }
        if (null == column.getColumnType()) {
            ColumnType columnType = type(column.getTypeName());
            column.setColumnType(columnType);
        }
    }

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

    @Override
    public Column column(DataRuntime runtime, Column column, ResultSetMetaData rsm, int index) {
        if (null == column) {
            column = new Column();
        }
        String catalog = null;
        String schema = null;
        try {
            catalog = BasicUtil.evl(rsm.getCatalogName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getCatalogName]");
        }
        try {
            schema = BasicUtil.evl(rsm.getSchemaName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getSchemaName]");
        }
        checkSchema(column, catalog, schema);
        try {
            column.setClassName(rsm.getColumnClassName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnClassName]");
        }
        try {
            column.caseSensitive(rsm.isCaseSensitive(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:isCaseSensitive]");
        }
        try {
            column.currency(rsm.isCurrency(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:isCurrency]");
        }
        try {
            column.setOriginalName(rsm.getColumnName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnName]");
        }
        try {
            column.setName(rsm.getColumnLabel(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnLabel]");
        }
        try {
            column.setPrecision(rsm.getPrecision(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getPrecision]");
        }
        try {
            column.setScale(rsm.getScale(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getScale]");
        }
        try {
            column.setDisplaySize(rsm.getColumnDisplaySize(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnDisplaySize]");
        }
        try {
            column.setSigned(rsm.isSigned(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:isSigned]");
        }
        try {
            column.setTable(rsm.getTableName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getTableName]");
        }
        try {
            column.setType(rsm.getColumnType(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnType]");
        }
        try {
            //不准确 POINT 返回 GEOMETRY
            String jdbcType = rsm.getColumnTypeName(index);
            column.setJdbcType(jdbcType);
            if (BasicUtil.isEmpty(column.getTypeName())) {
                column.setTypeName(jdbcType);
            }
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getColumnTypeName]");
        }
        ColumnType columnType = type(column.getTypeName());
        column.setColumnType(columnType);
        return column;
    }


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

    @Override
    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, DatabaseMetaData dbmd, Table table, String pattern) throws Exception {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        Catalog catalog = table.getCatalog();
        Schema schema = table.getSchema();
        if (BasicUtil.isEmpty(table.getName())) {
            return columns;
        }
        String catalogName = null;
        String schemaName = null;
        if (null != catalog) {
            catalogName = catalog.getName();
        }
        if (null != schema) {
            schemaName = schema.getName();
        }
        String[] tmp = checkSchema(catalogName, schemaName);
        ResultSet set = dbmd.getColumns(tmp[0], tmp[1], table.getName(), pattern);
        Map<String, Integer> keys = keys(set);
        while (set.next()) {
            String name = set.getString("COLUMN_NAME");
            if (null == name) {
                continue;
            }
            String columnCatalog = string(keys, "TABLE_CAT", set, null);
            if (null != columnCatalog) {
                columnCatalog = columnCatalog.trim();
            }
            String columnSchema = string(keys, "TABLE_SCHEM", set, null);
            if (null != columnSchema) {
                columnSchema = columnSchema.trim();
            }


            T column = columns.get(name.toUpperCase());
            if (null == column) {
                if (create) {
                    column = (T) new Column(name);
                    columns.put(name.toUpperCase(), column);
                } else {
                    continue;
                }
            }

            checkSchema(column, columnCatalog, columnSchema);
            if (!BasicUtil.equalsIgnoreCase(catalog, column.getCatalogName())) {
                continue;
            }
            if (!BasicUtil.equalsIgnoreCase(schema, column.getSchemaName())) {
                continue;
            }


            String remark = string(keys, "REMARKS", set, column.getComment());
            if ("TAG".equals(remark)) {
                column = (T) new Tag();
            }
            column.setComment(remark);
            column.setTable(BasicUtil.evl(string(keys, "TABLE_NAME", set, table.getName()), column.getTableName(true)));
            column.setType(integer(keys, "DATA_TYPE", set, column.getType()));
            column.setType(integer(keys, "SQL_DATA_TYPE", set, column.getType()));
            String jdbcType = string(keys, "TYPE_NAME", set, column.getTypeName());
            if (BasicUtil.isEmpty(column.getTypeName())) {
                //数据库中 有jdbc是支持的类型 如果数据库中有了就不用jdbc的了
                column.setTypeName(jdbcType);
            }
            column.setJdbcType(jdbcType);
            column.setPrecision(integer(keys, "COLUMN_SIZE", set, column.getPrecision()));
            column.setScale(integer(keys, "DECIMAL_DIGITS", set, column.getScale()));
            column.nullable(bool(keys, "NULLABLE", set, column.isNullable()));
            column.setDefaultValue(value(keys, "COLUMN_DEF", set, column.getDefaultValue()));
            column.setPosition(integer(keys, "ORDINAL_POSITION", set, column.getPosition()));
            column.autoIncrement(bool(keys, "IS_AUTOINCREMENT", set, column.isAutoIncrement()));
            ColumnType columnType = type(column.getTypeName());
            column.setColumnType(columnType);
            column(runtime, column, set);
            column.setName(name);
        }

        // 主键
        ResultSet rs = dbmd.getPrimaryKeys(tmp[0], tmp[1], table.getName());
        while (rs.next()) {
            String name = rs.getString(4);
            Column column = columns.get(name.toUpperCase());
            if (null == column) {
                continue;
            }
            column.primary(true);
        }
        return columns;
    }


    /**
     * column[结果集封装-子流程](方法3)<br/>
     * 方法(3)内部遍历
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  column
     * @param rs      ResultSet
     * @return Column
     */
    @Override
    public Column column(DataRuntime runtime, Column column, ResultSet rs) {
        if (null == column) {
            column = new Column();
        }
        try {
            Map<String, Integer> keys = keys(rs);
            if (null == column.getName()) {
                column.setName(string(keys, "COLUMN_NAME", rs));
            }
            if (null == column.getType()) {
                column.setType(BasicUtil.parseInt(string(keys, "DATA_TYPE", rs), null));
            }
            if (null == column.getType()) {
                column.setType(BasicUtil.parseInt(string(keys, "SQL_DATA_TYPE", rs), null));
            }
            if (null == column.getTypeName()) {
                String jdbcType = string(keys, "TYPE_NAME", rs);
                column.setJdbcType(jdbcType);
                if (BasicUtil.isEmpty(column.getTypeName())) {
                    column.setTypeName(jdbcType);
                }
            }
            if (null == column.getPrecision()) {
                column.setPrecision(integer(keys, "COLUMN_SIZE", rs, null));
            }
            if (null == column.getScale()) {
                column.setScale(BasicUtil.parseInt(string(keys, "DECIMAL_DIGITS", rs), null));
            }
            if (null == column.getPosition()) {
                column.setPosition(BasicUtil.parseInt(string(keys, "ORDINAL_POSITION", rs), 0));
            }
            if (-1 == column.isAutoIncrement()) {
                column.autoIncrement(BasicUtil.parseBoolean(string(keys, "IS_AUTOINCREMENT", rs), false));
            }
            if (-1 == column.isGenerated()) {
                column.generated(BasicUtil.parseBoolean(string(keys, "IS_GENERATEDCOLUMN", rs), false));
            }
            if (null == column.getComment()) {
                column.setComment(string(keys, "REMARKS", rs));
            }
            if (null == column.getPosition()) {
                column.setPosition(BasicUtil.parseInt(string(keys, "ORDINAL_POSITION", rs), 0));
            }
            if (BasicUtil.isEmpty(column.getDefaultValue())) {
                column.setDefaultValue(string(keys, "COLUMN_DEF", rs));
            }
            ColumnType columnType = type(column.getTypeName());
            column.setColumnType(columnType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return column;
    }


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
    @Override
    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, Table table, SqlRowSet set) throws Exception {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        SqlRowSetMetaData rsm = set.getMetaData();
        for (int i = 1; i <= rsm.getColumnCount(); i++) {
            String name = rsm.getColumnName(i);
            if (BasicUtil.isEmpty(name)) {
                continue;
            }
            T column = columns.get(name.toUpperCase());
            if (null == column) {
                if (create) {
                    column = (T) column(runtime, column, rsm, i);
                    if (BasicUtil.isEmpty(column.getName())) {
                        column.setName(name);
                    }
                    columns.put(column.getName().toUpperCase(), column);
                }
            }
        }
        return columns;
    }

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
    @Override
    public Column column(DataRuntime runtime, Column column, SqlRowSetMetaData rsm, int index) {
        if (null == column) {
            column = new Column();
            String catalog = null;
            String schema = null;
            try {
                catalog = BasicUtil.evl(rsm.getCatalogName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getCatalogName]");
            }
            try {
                schema = BasicUtil.evl(rsm.getSchemaName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getSchemaName]");
            }
            checkSchema(column, catalog, schema);
            try {
                column.setClassName(rsm.getColumnClassName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnClassName]");
            }
            try {
                column.currency(rsm.isCurrency(index));
            } catch (Exception e) {
                column.caseSensitive(rsm.isCaseSensitive(index));
                log.debug("[获取MetaData失败][驱动未实现:isCurrency]");
            }
            try {
                column.setOriginalName(rsm.getColumnName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnName]");
            }
            try {
                column.setName(rsm.getColumnLabel(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnLabel]");
            }
            try {
                column.setPrecision(rsm.getPrecision(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getPrecision]");
            }
            try {
                column.setScale(rsm.getScale(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getScale]");
            }
            try {
                column.setDisplaySize(rsm.getColumnDisplaySize(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnDisplaySize]");
            }
            try {
                column.setSigned(rsm.isSigned(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:isSigned]");
            }
            try {
                column.setTable(rsm.getTableName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getTableName]");
            }
            try {
                column.setType(rsm.getColumnType(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnType]");
            }
            try {
                String jdbcType = rsm.getColumnTypeName(index);
                column.setJdbcType(jdbcType);
                if (BasicUtil.isEmpty(column.getTypeName())) {
                    column.setTypeName(jdbcType);
                }
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getColumnTypeName]");
            }

            ColumnType columnType = type(column.getTypeName());
            column.setColumnType(columnType);
        }
        return column;
    }

    protected long batch(JdbcTemplate jdbc, String sql, int batch, int vol, List<Object> values) {
        int size = values.size(); //一共多少参数
        int line = size / vol; //一共多少行
        //batch insert保持SQL一致,如果不一致应该调用save方法
        //返回每个SQL的影响行数
        jdbc.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        //i从0开始 参数下标从1开始
                        for (int p = 1; p <= vol; p++) {
                            ps.setObject(p, values.get(vol * i + p - 1));
                        }
                    }

                    public int getBatchSize() {
                        return line;
                    }
                });
        return line;
    }


    @Override
    public <T extends BaseMetadata> void checkSchema(DataRuntime runtime, T meta) {
        if (null != meta) {
            Connection con = null;
            try {
                if (null == meta.getCatalog() || null == meta.getSchema()) {
                    con = runtime.datasource().getConnection();
                    String catalog = null;
                    String schema = null;
                    if (null == meta.getCatalog()) {
                        catalog = con.getCatalog();
                    }
                    if (null == meta.getSchema()) {
                        schema = con.getSchema();
                    }
                    checkSchema(meta, catalog, schema);
                    meta.setCheckSchemaTime(new Date());
                }
            } catch (Exception e) {
                log.error("[check schema][fail:{}]", e.toString());
            } finally {
                if (null != con) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    @Override
    public LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, String random, String name) {
        return null;
    }

    @Override
    public List<Catalog> catalogs(DataRuntime runtime, String random, boolean greedy, String name) {
        return null;
    }

    @Override
    public List<Run> buildQueryCatalogRun(DataRuntime runtime, boolean greedy, String name) throws Exception {
        return null;
    }

    @Override
    public LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, int index, boolean create, LinkedHashMap<String, Catalog> catalogs, DataSet set) throws Exception {
        return null;
    }

    @Override
    public List<Catalog> catalogs(DataRuntime runtime, int index, boolean create, List<Catalog> catalogs, DataSet set) throws Exception {
        return null;
    }

    @Override
    public LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, boolean create, LinkedHashMap<String, Catalog> catalogs) throws Exception {
        return null;
    }

    @Override
    public List<Catalog> catalogs(DataRuntime runtime, boolean create, List<Catalog> catalogs) throws Exception {
        return null;
    }

    @Override
    public Catalog catalog(DataRuntime runtime, int index, boolean create, DataSet set) throws Exception {
        return null;
    }


    public <T extends Column> T column(Catalog catalog, Schema schema, String table, String name, List<T> columns) {
        for (T column : columns) {
            if (null != table && null != name) {
                String identity = BasicUtil.nvl(catalog, "") + "_" + BasicUtil.nvl(schema, "") + "_" + BasicUtil.nvl(table, "") + "_" + name;
                identity = MD5Util.crypto(identity.toUpperCase());
                if (identity.equals(column.getIdentity())) {
                    return column;
                }
            }
        }
        return null;
    }

    public <T extends Column> T column(T column, List<T> columns) {
        for (T item : columns) {
            if (item.getIdentity().equals(column.getIdentity())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取ResultSet中的列
     *
     * @param set ResultSet
     * @return list
     * @throws Exception 异常 Exception
     */
    protected Map<String, Integer> keys(ResultSet set) throws Exception {
        ResultSetMetaData rsmd = set.getMetaData();
        Map<String, Integer> keys = new HashMap<>();
        if (null != rsmd) {
            for (int i = 1; i < rsmd.getColumnCount(); i++) {
                String name = rsmd.getColumnLabel(i);
                if (null == name) {
                    name = rsmd.getColumnName(i);
                }
                keys.put(name.toUpperCase(), i);
            }
        }
        return keys;
    }

    /**
     * 生成insert sql的value部分,每个Entity(每行数据)调用一次
     * (1,2,3)
     * (?,?,?)
     *
     * @param runtime     运行环境主要包含驱动适配器 数据源或客户端
     * @param run         run
     * @param obj         Entity或DataRow
     * @param placeholder 是否使用占位符(批量操作时不要超出数量)
     * @param scope       是否带(), 拼接在select后时不需要
     * @param alias       是否添加别名
     * @param columns     需要插入的列
     * @param child       是否在子查询中，子查询中不要用序列
     */
    protected String insertValue(DataRuntime runtime, Run run, Object obj, boolean child, boolean placeholder, boolean alias, boolean scope, LinkedHashMap<String, Column> columns) {
        int batch = run.getBatch();
        StringBuilder builder = new StringBuilder();
        if (scope && batch <= 1) {
            builder.append("(");
        }
        int from = 1;
        if (obj instanceof DataRow) {
            from = 1;
        }
        run.setFrom(from);
        boolean first = true;
        for (Column column : columns.values()) {
            boolean place = placeholder;
            boolean src = false; //直接拼接 如${now()} ${序列}
            String key = column.getName();
            if (!first && batch <= 1) {
                builder.append(",");
            }
            first = false;
            Object value = null;
            if (obj instanceof DataRow) {
                value = BeanUtil.getFieldValue(obj, key);
            } else if (obj instanceof Map) {
                value = ((Map) obj).get(key);
            } else {
                value = BeanUtil.getFieldValue(obj, EntityAdapterProxy.field(obj.getClass(), key));
            }
            if (value != null) {
                if (value instanceof SQL_BUILD_IN_VALUE) {
                    place = false;
                } else if (value instanceof String) {
                    String str = (String) value;
                    //if(str.startsWith("${") && str.endsWith("}")){
                    if (BasicUtil.checkEl(str)) {
                        src = true;
                        place = false;
                        value = str.substring(2, str.length() - 1);
                        if (child && str.toUpperCase().contains(".NEXTVAL")) {
                            value = null;
                        }
                    } else if ("NULL".equals(str)) {
                        value = null;
                    }
                }
            }
            if (src) {
                builder.append(value);
            } else {
                if (batch <= 1) {
                    if (place) {
                        builder.append("?");
                        addRunValue(runtime, run, Compare.EQUAL, column, value);
                    } else {
                        //value(runtime, builder, obj, key);
                        builder.append(write(runtime, null, value, false));
                    }
                } else {
                    addRunValue(runtime, run, Compare.EQUAL, column, value);
                }
            }

            if (alias && batch <= 1) {
                builder.append(" AS ").append(key);
            }
        }
        if (scope && batch <= 1) {
            builder.append(")");
        }
        return builder.toString();
    }

    public String getPrimayKey(Object obj) {
        String key = null;
        if (obj instanceof Collection) {
            obj = ((Collection) obj).iterator().next();
        }
        if (obj instanceof DataRow) {
            key = ((DataRow) obj).getPrimaryKey();
        } else {
            key = EntityAdapterProxy.primaryKey(obj.getClass(), true);
        }
        return key;
    }

    /**
     * 拼接字符串
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param args    args
     * @return String
     */
    @Override
    public String concat(DataRuntime runtime, String... args) {
        return null;
    }

    /**
     * 伪表
     *
     * @return String
     */
    protected String dummy() {
        return "dual";
    }
    /* *****************************************************************************************************************
     * 													多分支子类型选择(子类只选择调用不要出现不要覆盖)
     * -----------------------------------------------------------------------------------------------------------------
     * protected String pageXXX()
     * protected String concatXXX()
     ******************************************************************************************************************/



    protected String concatFun(DataRuntime runtime, String... args) {
        String result = "";
        if (null != args && args.length > 0) {
            result = "concat(";
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result += ",";
                }
                result += arg;
            }
            result += ")";
        }
        return result;
    }

    protected String concatOr(DataRuntime runtime, String... args) {
        String result = "";
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result += " || ";
                }
                result += arg;
            }
        }
        return result;
    }

    protected String concatAdd(DataRuntime runtime, String... args) {
        String result = "";
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result += " + ";
                }
                result += arg;
            }
        }
        return result;
    }

    protected String concatAnd(DataRuntime runtime, String... args) {
        String result = "";
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result += " & ";
                }
                result += arg;
            }
        }
        return result;
    }


}
