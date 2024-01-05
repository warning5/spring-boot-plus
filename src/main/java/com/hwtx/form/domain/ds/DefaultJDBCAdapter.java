package com.hwtx.form.domain.ds;


import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.TableType;
import com.hwtx.form.domain.ds.metadata.*;
import com.hwtx.form.util.BasicUtil;
import com.hwtx.form.util.MD5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
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

    protected void init(Table table, ResultSet set, Map<String, Integer> keys) {
        try {
            table.setType(com.hwtx.form.util.BasicUtil.evl(string(keys, "TABLE_TYPE", set), table.getType()));
        } catch (Exception e) {
        }
        try {
            table.setComment(com.hwtx.form.util.BasicUtil.evl(string(keys, "REMARKS", set), table.getComment()));
        } catch (Exception e) {
        }
        try {
            table.setTypeCat(com.hwtx.form.util.BasicUtil.evl(string(keys, "TYPE_CAT", set), table.getTypeCat()));
        } catch (Exception e) {
        }
        try {
            table.setTypeName(com.hwtx.form.util.BasicUtil.evl(string(keys, "TYPE_NAME", set), table.getTypeName()));
        } catch (Exception e) {
        }
        try {
            table.setSelfReferencingColumn(com.hwtx.form.util.BasicUtil.evl(string(keys, "SELF_REFERENCING_COL_NAME", set), table.getSelfReferencingColumn()));
        } catch (Exception e) {
        }
        try {
            table.setRefGeneration(com.hwtx.form.util.BasicUtil.evl(string(keys, "REF_GENERATION", set), table.getRefGeneration()));
        } catch (Exception e) {
        }

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

    @Override
    public HashMap<String, Column> columns(DataRuntime runtime, Table table, String pattern) throws Exception {
        HashMap<String, Column> columns = new LinkedHashMap<>();
        DataSource ds = null;
        Connection con = null;
        try {
            JdbcTemplate jdbc = jdbc(runtime);
            ds = jdbc.getDataSource();
            assert ds != null;
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
            assert ds != null;
            con = DataSourceUtils.getConnection(ds);
            DatabaseMetaData dbmd = con.getMetaData();
            checkSchema(runtime, table);
            String[] tmp = checkSchema(table.getCatalogName(), table.getSchemaName());
            ResultSet set = dbmd.getIndexInfo(tmp[0], tmp[1], table.getName(), false, false);
            Map<String, Integer> keys = keys(set);
            Map<String, Column> columns;
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
                    String catalog = com.hwtx.form.util.BasicUtil.evl(string(keys, "TABLE_CATALOG", set), string(keys, "TABLE_CAT", set));
                    String schema = com.hwtx.form.util.BasicUtil.evl(string(keys, "TABLE_SCHEMA", set), string(keys, "TABLE_SCHEM", set));
                    checkSchema(index, catalog, schema);
                    if (!com.hwtx.form.util.BasicUtil.equals(table.getCatalogName(), index.getCatalogName()) || !com.hwtx.form.util.BasicUtil.equals(table.getSchemaName(), index.getSchemaName())) {
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
        if (com.hwtx.form.util.BasicUtil.isEmpty(sql)) {
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
            HwtxSQLUpdateException ex = new HwtxSQLUpdateException("update异常:" + e.toString(), e);
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
        Map<String, Column> columnsMap = meta.getColumns();
        Collection<Column> columns = null;
        Map<String, Column> pks = null;
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
        Map<String, Column> pks;
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
                if (com.hwtx.form.util.BasicUtil.isNotEmpty(order)) {
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
            if (!com.hwtx.form.util.BasicUtil.equalsIgnoreCase(name, uname) && !uname.endsWith("_TMP_UPDATE_TYPE")) {
                runs.addAll(buildRenameRun(runtime, meta));
            }
            // 修改数据类型
            String type = type(runtime, null, meta).toString();
            String utype = type(runtime, null, update).toString();
            boolean exe = false;
            if (!com.hwtx.form.util.BasicUtil.equalsIgnoreCase(type, utype)) {
                List<Run> list = buildChangeTypeRun(runtime, meta);
                if (null != list) {
                    runs.addAll(list);
                    exe = true;
                }
            } else {
                //数据类型没变但长度变了
                if (!Objects.equals(meta.getPrecision(), update.getPrecision()) || !Objects.equals(meta.getScale(), update.getScale())) {
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
            if (!com.hwtx.form.util.BasicUtil.equalsIgnoreCase(def, udef)) {
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
            if (!com.hwtx.form.util.BasicUtil.equalsIgnoreCase(comment, ucomment)) {
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
            Table table = meta.getTable();
            List<Run> slices = new ArrayList<>();
            if (null != origin) {
                slices.addAll(buildDropRun(runtime, origin, true));
            }
            slices.addAll(buildAddRun(runtime, meta, true));
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
        if (com.hwtx.form.util.BasicUtil.isEmpty(name)) {
            name = "index_" + com.hwtx.form.util.BasicUtil.getRandomString(10);
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
            if (com.hwtx.form.util.BasicUtil.isNotEmpty(order)) {
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
            if (com.hwtx.form.util.BasicUtil.isNotEmpty(table)) {
                builder.append(" ON ");
                name(runtime, builder, table);
            }
        }
        return runs;
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
            catalog = com.hwtx.form.util.BasicUtil.evl(rsm.getCatalogName(index));
        } catch (Exception e) {
            log.debug("[获取MetaData失败][驱动未实现:getCatalogName]");
        }
        try {
            schema = com.hwtx.form.util.BasicUtil.evl(rsm.getSchemaName(index));
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
            if (com.hwtx.form.util.BasicUtil.isEmpty(column.getTypeName())) {
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
                column.setType(com.hwtx.form.util.BasicUtil.parseInt(string(keys, "DATA_TYPE", rs), null));
            }
            if (null == column.getType()) {
                column.setType(com.hwtx.form.util.BasicUtil.parseInt(string(keys, "SQL_DATA_TYPE", rs), null));
            }
            if (null == column.getTypeName()) {
                String jdbcType = string(keys, "TYPE_NAME", rs);
                column.setJdbcType(jdbcType);
                if (com.hwtx.form.util.BasicUtil.isEmpty(column.getTypeName())) {
                    column.setTypeName(jdbcType);
                }
            }
            if (null == column.getPrecision()) {
                column.setPrecision(integer(keys, "COLUMN_SIZE", rs, null));
            }
            if (null == column.getScale()) {
                column.setScale(com.hwtx.form.util.BasicUtil.parseInt(string(keys, "DECIMAL_DIGITS", rs), null));
            }
            if (null == column.getPosition()) {
                column.setPosition(com.hwtx.form.util.BasicUtil.parseInt(string(keys, "ORDINAL_POSITION", rs), 0));
            }
            if (-1 == column.isAutoIncrement()) {
                column.autoIncrement(com.hwtx.form.util.BasicUtil.parseBoolean(string(keys, "IS_AUTOINCREMENT", rs), false));
            }
            if (-1 == column.isGenerated()) {
                column.generated(com.hwtx.form.util.BasicUtil.parseBoolean(string(keys, "IS_GENERATEDCOLUMN", rs), false));
            }
            if (null == column.getComment()) {
                column.setComment(string(keys, "REMARKS", rs));
            }
            if (null == column.getPosition()) {
                column.setPosition(com.hwtx.form.util.BasicUtil.parseInt(string(keys, "ORDINAL_POSITION", rs), 0));
            }
            if (com.hwtx.form.util.BasicUtil.isEmpty(column.getDefaultValue())) {
                column.setDefaultValue(string(keys, "COLUMN_DEF", rs));
            }
            ColumnType columnType = type(column.getTypeName());
            column.setColumnType(columnType);
        } catch (Exception e) {
            log.error("column", e);
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
     */
    @Override
    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, boolean create, LinkedHashMap<String, T> columns, Table table, SqlRowSet set) throws Exception {
        if (null == columns) {
            columns = new LinkedHashMap<>();
        }
        SqlRowSetMetaData rsm = set.getMetaData();
        for (int i = 1; i <= rsm.getColumnCount(); i++) {
            String name = rsm.getColumnName(i);
            if (com.hwtx.form.util.BasicUtil.isEmpty(name)) {
                continue;
            }
            T column = columns.get(name.toUpperCase());
            if (null == column) {
                if (create) {
                    column = (T) column(runtime, column, rsm, i);
                    if (com.hwtx.form.util.BasicUtil.isEmpty(column.getName())) {
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
                catalog = com.hwtx.form.util.BasicUtil.evl(rsm.getCatalogName(index));
            } catch (Exception e) {
                log.debug("[获取MetaData失败][驱动未实现:getCatalogName]");
            }
            try {
                schema = com.hwtx.form.util.BasicUtil.evl(rsm.getSchemaName(index));
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
                if (com.hwtx.form.util.BasicUtil.isEmpty(column.getTypeName())) {
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
                    public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
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
    public <T extends BaseMetadata<T>> void checkSchema(DataRuntime runtime, T meta) {
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

    public <T extends Column> T column(Catalog catalog, Schema schema, String table, String name, List<T> columns) {
        for (T column : columns) {
            if (null != table && null != name) {
                String identity = com.hwtx.form.util.BasicUtil.nvl(catalog, "") + "_" + com.hwtx.form.util.BasicUtil.nvl(schema, "") + "_" + BasicUtil.nvl(table, "") + "_" + name;
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


    protected String concatFun(String... args) {
        StringBuilder result = new StringBuilder();
        if (null != args && args.length > 0) {
            result = new StringBuilder("concat(");
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result.append(",");
                }
                result.append(arg);
            }
            result.append(")");
        }
        return result.toString();
    }

    protected String concatOr(String... args) {
        StringBuilder result = new StringBuilder();
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result.append(" || ");
                }
                result.append(arg);
            }
        }
        return result.toString();
    }

    protected String concatAdd(String... args) {
        StringBuilder result = new StringBuilder();
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result.append(" + ");
                }
                result.append(arg);
            }
        }
        return result.toString();
    }

    protected String concatAnd(String... args) {
        StringBuilder result = new StringBuilder();
        if (null != args && args.length > 0) {
            int size = args.length;
            for (int i = 0; i < size; i++) {
                String arg = args[i];
                if (i > 0) {
                    result.append(" & ");
                }
                result.append(arg);
            }
        }
        return result.toString();
    }
}
