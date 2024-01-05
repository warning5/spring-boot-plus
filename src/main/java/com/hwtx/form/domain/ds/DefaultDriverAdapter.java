package com.hwtx.form.domain.ds;


import com.google.common.collect.Maps;
import com.hwtx.form.domain.ds.metadata.*;
import com.hwtx.form.util.BasicUtil;
import com.hwtx.form.util.LogUtil;
import com.hwtx.form.util.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;


/**
 * SQL生成 子类主要实现与分页相关的SQL 以及delimiter
 */

@Slf4j
public abstract class DefaultDriverAdapter implements DriverAdapter {
    public String delimiterFr = "";
    public String delimiterTo = "";

    //根据名称定准数据类型
    protected Map<String, ColumnType> types = Maps.newHashMap();

    public DefaultDriverAdapter() {
        //当前数据库支持的数据类型,子类根据情况覆盖
        for (StandardColumnType type : StandardColumnType.values()) {
            DatabaseType[] dbs = type.dbs();
            for (DatabaseType db : dbs) {
                if (db == this.type()) {
                    //column type支持当前db
                    types.put(type.getName(), type);
                    break;
                }
            }
        }
    }

    @Override
    public String getDelimiterFr() {
        return this.delimiterFr;
    }

    @Override
    public String getDelimiterTo() {
        return this.delimiterTo;
    }

    public void setDelimiter(String delimiter) {
        if (com.hwtx.form.util.BasicUtil.isNotEmpty(delimiter)) {
            delimiter = delimiter.replaceAll("\\s", "");
            if (delimiter.length() == 1) {
                this.delimiterFr = delimiter;
                this.delimiterTo = delimiter;
            } else {
                this.delimiterFr = delimiter.substring(0, 1);
                this.delimiterTo = delimiter.substring(1, 2);
            }
        }
    }

    /**
     * catalog[命令合成]<br/>
     * 查询所有数据库
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param name    名称统配符或正则
     * @param greedy  贪婪模式 true:查询权限范围内尽可能多的数据
     * @return sqls
     * @throws Exception 异常
     */
    public List<Run> buildQuerySchemaRun(DataRuntime runtime, boolean greedy, Catalog catalog, String name) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQuerySchemaRun(DataRuntime runtime, boolean greedy, Catalog catalog, String name)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * schema[结果集封装]<br/>
     * 根据驱动内置接口补充 Schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param schemas 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    public LinkedHashMap<String, Schema> schemas(DataRuntime runtime, boolean create, LinkedHashMap<String, Schema> schemas) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 LinkedHashMap<String, Schema> schemas(DataRuntime runtime, boolean create, LinkedHashMap<String, Schema> schemas)", 37));
        }
        return new LinkedHashMap<>();
    }

    /**
     * schema[结果集封装]<br/>
     * 根据驱动内置接口补充 Schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param schemas 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    public List<Schema> schemas(DataRuntime runtime, boolean create, List<Schema> schemas) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Schema> schemas(DataRuntime runtime, boolean create, List<Schema> schemas)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * table[命令合成]<br/>
     * 查询表,不是查表中的数据
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param catalog catalog
     * @param schema  schema
     * @param pattern 名称统配符或正则
     * @param types   "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     * @return String
     */
    @Override
    public List<Run> buildQueryTablesRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQueryTableRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types)", 37));
        }
        return new ArrayList<>();
    }


    /**
     * table[命令合成]<br/>
     * 查询表备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param catalog catalog
     * @param schema  schema
     * @param pattern 名称统配符或正则
     * @param types   types "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     * @return String
     */
    public List<Run> buildQueryTableCommentRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQueryTableCommentRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQueryColumnRun(DataRuntime runtime, Table table, boolean metadata)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * primary[命令合成]<br/>
     * 查询表上的主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sqls
     */
    public List<Run> buildQueryPrimaryRun(DataRuntime runtime, Table table) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQueryPrimaryRun(DataRuntime runtime, Table table)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * index[命令合成]<br/>
     * 查询表上的索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param name    名称
     * @return sqls
     */
    @Override
    public List<Run> buildQueryIndexRun(DataRuntime runtime, Table table, String name) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildQueryIndexRun(DataRuntime runtime, Table table, String name)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * 根据 catalog, schema, name检测tables集合中是否存在
     *
     * @param tables  tables
     * @param catalog catalog
     * @param schema  schema
     * @param name    name
     * @param <T>     Table
     * @return 如果存在则返回Table 不存在则返回null
     */
    public <T extends Table> T table(List<T> tables, Catalog catalog, Schema schema, String name) {
        if (null != tables) {
            for (T table : tables) {
                if ((null == catalog || catalog.equal(table.getCatalog())) && (null == schema || schema.equal(table.getSchema())) && table.getName().equalsIgnoreCase(name)) {
                    return table;
                }
            }
        }
        return null;
    }

    /**
     * 根据 catalog, name检测schemas集合中是否存在
     *
     * @param schemas schemas
     * @param catalog catalog
     * @param name    name
     * @param <T>     Table
     * @return 如果存在则返回 Schema 不存在则返回null
     */
    public <T extends Schema> T schema(List<T> schemas, Catalog catalog, String name) {
        if (null != schemas) {
            for (T schema : schemas) {
                if ((null == catalog || catalog.equal(schema.getCatalog())) && schema.getName().equalsIgnoreCase(name)) {
                    return schema;
                }
            }
        }
        return null;
    }

    /**
     * 根据 name检测catalogs集合中是否存在
     *
     * @param catalogs catalogs
     * @param name     name
     * @param <T>      Table
     * @return 如果存在则返回 Catalog 不存在则返回null
     */
    public <T extends Catalog> T catalog(List<T> catalogs, String name) {
        if (null != catalogs) {
            for (T catalog : catalogs) {
                if (catalog.getName().equalsIgnoreCase(name)) {
                    return catalog;
                }
            }
        }
        return null;
    }

    /**
     * 根据 name检测databases集合中是否存在
     *
     * @param databases databases
     * @param name      name
     * @param <T>       Table
     * @return 如果存在则返回 Database 不存在则返回null
     */
    public <T extends Database> T database(List<T> databases, String name) {
        if (null != databases) {
            for (T database : databases) {
                if (database.getName().equalsIgnoreCase(name)) {
                    return database;
                }
            }
        }
        return null;
    }

    /**
     * table[调用入口]<br/>
     * 创建表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    public boolean create(DataRuntime runtime, Table meta) throws Exception {
        boolean result = false;
        checkSchema(runtime, meta);
        List<Run> runs = buildCreateRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
            result = execute(runtime, meta, runs);
        } finally {
            log.info("[name:{}][cmds:{}][result:{}][执行耗时:{}ms]", meta.getName(), runs.size(), result, System.currentTimeMillis() - fr);
        }
        return result;
    }

    public boolean alter(DataRuntime runtime, Table meta) throws Exception {
        boolean result = true;
        List<Run> runs = new ArrayList<>();
        Table update = (Table) meta.getUpdate();

        String name = meta.getName();
        String uname = update.getName();
        checkSchema(runtime, meta);
        checkSchema(runtime, update);
        if (!name.equalsIgnoreCase(uname)) {
            result = rename(runtime, meta, uname);
            meta.setName(uname);
        }
        if (!result) {
            return result;
        }
        //修改表备注
        String comment = update.getComment();
        if (!comment.equals(meta.getComment())) {
            if (com.hwtx.form.util.BasicUtil.isNotEmpty(meta.getComment())) {
                runs.addAll(buildChangeCommentRun(runtime, update));
            } else {
                runs.addAll(buildAppendCommentRun(runtime, update));
            }
            long fr = System.currentTimeMillis();
            try {
                result = execute(runtime, meta, runs);
            } finally {
                long millis = System.currentTimeMillis() - fr;
                log.info("修改表信息成功 table = {}，消耗 = {}", meta, millis);

            }
        }
        return result;
    }

    /**
     * table[调用入口]<br/>
     * 删除表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */

    public boolean drop(DataRuntime runtime, Table meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.TABLE_DROP;
        String random = random(runtime);

        checkSchema(runtime, meta);
        List<Run> runs = buildDropRun(runtime, meta);

        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, meta, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getName(), runs.size(), result, millis);
            }

        }
        return result;
    }

    /**
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  原表
     * @param name    新名称
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */

    public boolean rename(DataRuntime runtime, Table origin, String name) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.TABLE_RENAME;
        String random = random(runtime);
        origin.setNewName(name);
        checkSchema(runtime, origin);
        List<Run> runs = buildRenameRun(runtime, origin);
        long fr = System.currentTimeMillis();
        try {
            result = execute(runtime, origin, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][name:{}][rename:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, origin.getName(), name, runs.size(), result, millis);
            }
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
    public String keyword(Table meta) {
        return meta.getKeyword();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildChangeCommentRun(DataRuntime runtime, Table meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder primary(DataRuntime runtime, StringBuilder builder, Table meta)", 37));
        }
        return builder;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    表
     * @return StringBuilder
     */
    public StringBuilder charset(DataRuntime runtime, StringBuilder builder, Table meta) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder charset(DataRuntime runtime, StringBuilder builder, Table meta)", 37));
        }
        return builder;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    表
     * @return StringBuilder
     */
    @Override
    public StringBuilder comment(DataRuntime runtime, StringBuilder builder, Table meta) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder comment(DataRuntime runtime, StringBuilder builder, Table meta)", 37));
        }
        return builder;
    }

    @Override
    public boolean alter(DataRuntime runtime, Table table, Column column) throws Exception {
        boolean result;
        checkSchema(runtime, table);
        if (column.isDrop()) {
            column.setAction(ACTION.DDL.COLUMN_DROP);
        } else {
            HashMap<String, Column> columns = columns(runtime, table, null);
            Column existColumn = columns.get(column.getName());
            Column target = column.getUpdate();
            if (target == null) {
                throw new RuntimeException("修改列表信息失败，无法获取修改后信息 column = " + column.getName());
            }
            if (target.equals(existColumn)) {
                log.error("数据列数据未修改 source = {},target = {}", existColumn, target);
                return false;
            }
            if (existColumn != null) {
                column = existColumn;
                column.setAction(ACTION.DDL.COLUMN_ALTER);
            } else {
                column.setAction(ACTION.DDL.COLUMN_ADD);
            }
        }
        fillSchema(table, column);
        column.setTable(table);
        List<Run> runs = buildAlterRun(runtime, table, Collections.singleton(column));
        long fr = System.currentTimeMillis();
        try {
            result = execute(runtime, table, runs);
        } catch (Exception e) {
            log.error("[修改Column执行异常] table = {},column = {}", table, column);
            throw e;
        } finally {
            long millis = System.currentTimeMillis() - fr;
            log.info("执行修改表的列数据成功，table = {},column = {},[执行耗时:{}ms]", table.getTableName(), column.getName(), millis);
        }
        return result;
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
    public boolean drop(DataRuntime runtime, Column meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.COLUMN_DROP;
        String random = random(runtime);
        checkSchema(runtime, meta);
        List<Run> runs = buildDropRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, meta, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getTableName(true), meta.getName(), runs.size(), result, millis);
            }
        }
        return result;
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
    public boolean rename(DataRuntime runtime, Column origin, String name) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.COLUMN_RENAME;
        String random = random(runtime);
        origin.setNewName(name);
        checkSchema(runtime, origin);
        List<Run> runs = buildRenameRun(runtime, origin);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, origin, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][rename:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, origin.getTableName(true), origin.getName(), name, runs.size(), result, millis);
            }

        }
        return result;
    }


    /**
     * column[命令合成]<br/>
     * 添加列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Column meta, boolean slice) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAddRun(DataRuntime runtime, Column meta, boolean slice)", 37));
        }
        return new ArrayList<>();
    }

    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Column meta) throws Exception {
        return buildAddRun(runtime, meta, false);
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildDropRun(DataRuntime runtime, Column meta, boolean slice)", 37));
        }
        return new ArrayList<>();
    }

    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Column meta) throws Exception {
        return buildDropRun(runtime, meta, false);
    }

    /**
     * column[命令合成]<br/>
     * 修改列名
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildRenameRun(DataRuntime runtime, Column meta) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildRenameRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
    }


    /**
     * column[命令合成-子流程]<br/>
     * 修改数据类型
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return String
     */
    @Override
    public List<Run> buildChangeTypeRun(DataRuntime runtime, Column meta) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildChangeTypeRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 String alterColumnKeyword(DataRuntime runtime)", 37));
        }
        return null;
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
    public StringBuilder addColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder addColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
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
    public StringBuilder dropColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder dropColumnGuide(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildChangeDefaultRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildChangeNullableRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildChangeCommentRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * column[命令合成-子流程]<br/>
     * 添加表备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column 列
     * @return sql
     * @throws Exception 异常
     */
    /**
     * 添加表备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     *
     * @param meta 列
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildAppendCommentRun(DataRuntime runtime, Column meta) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAppendCommentRun(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
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
    public List<Run> buildDropAutoIncrement(DataRuntime runtime, Column meta) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildDropAutoIncrement(DataRuntime runtime, Column meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder define(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 checkColumnExists(DataRuntime runtime, StringBuilder builder, boolean exists)", 37));
        }
        return builder;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder type(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder type(DataRuntime runtime, StringBuilder builder, Column meta, String type, boolean isIgnorePrecision, boolean isIgnoreScale)", 37));
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 boolean isIgnorePrecision(DataRuntime runtime, Column meta)", 37));
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 boolean isIgnoreScale(DataRuntime runtime, Column meta)", 37));
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
        type = type.toUpperCase();
        if (type.contains("INT")) {
            return false;
        }
        if (type.contains("DATE")) {
            return true;
        }
        if (type.contains("TIME")) {
            return true;
        }
        if (type.contains("YEAR")) {
            return true;
        }
        if (type.contains("TEXT")) {
            return true;
        }
        if (type.contains("BLOB")) {
            return true;
        }
        if (type.contains("JSON")) {
            return true;
        }
        if (type.contains("POINT")) {
            return true;
        }
        if (type.contains("LINE")) {
            return true;
        }
        if (type.contains("POLYGON")) {
            return true;
        }
        if (type.contains("GEOMETRY")) {
            return true;
        }
        return null;
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
        type = type.toUpperCase();
        if (type.contains("INT")) {
            return true;
        }
        if (type.contains("DATE")) {
            return true;
        }
        if (type.contains("TIME")) {
            return true;
        }
        if (type.contains("YEAR")) {
            return true;
        }
        if (type.contains("TEXT")) {
            return true;
        }
        if (type.contains("BLOB")) {
            return true;
        }
        if (type.contains("JSON")) {
            return true;
        }
        if (type.contains("POINT")) {
            return true;
        }
        if (type.contains("LINE")) {
            return true;
        }
        if (type.contains("POLYGON")) {
            return true;
        }
        if (type.contains("GEOMETRY")) {
            return true;
        }
        return null;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder nullable(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder comment(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder comment(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder charset(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder defaultValue(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder primary(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
    }

    @Override
    public PrimaryKey primary(DataRuntime runtime, Table table) {
        throw new UnsupportedOperationException();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder increment(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 StringBuilder onupdate(DataRuntime runtime, StringBuilder builder, Column meta)", 37));
        }
        return builder;
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
    public boolean add(DataRuntime runtime, PrimaryKey meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.PRIMARY_ADD;
        String random = random(runtime);

        checkSchema(runtime, meta);
        List<Run> runs = buildAddRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, meta, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getTableName(true), meta.getName(), runs.size(), result, millis);
            }

        }
        return result;
    }

    /**
     * primary[调用入口]<br/>
     * 修改主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param origin  原主键
     * @param meta    新主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    public boolean alter(DataRuntime runtime, Table table, PrimaryKey origin, PrimaryKey meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.PRIMARY_ALTER;
        String random = random(runtime);

        checkSchema(runtime, meta);
        List<Run> runs = buildAlterRun(runtime, origin, meta);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, table, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getTableName(true), meta.getName(), runs.size(), result, millis);
            }
        }
        return result;
    }

    /**
     * primary[调用入口]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端```
     * @param meta    主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    public boolean drop(DataRuntime runtime, PrimaryKey meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.PRIMARY_DROP;
        String random = random(runtime);
        checkSchema(runtime, meta);
        List<Run> runs = buildDropRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, meta, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getTableName(true), meta.getName(), runs.size(), result, millis);
            }
        }
        return result;
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
    public boolean rename(DataRuntime runtime, PrimaryKey origin, String name) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.PRIMARY_RENAME;
        String random = random(runtime);
        origin.setNewName(name);
        checkSchema(runtime, origin);
        List<Run> runs = buildRenameRun(runtime, origin);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, origin, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][rename:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, origin.getTableName(true), origin.getName(), name, runs.size(), result, millis);
            }
        }
        return result;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAddRun(DataRuntime runtime, PrimaryKey meta,  boolean slice)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAlterRun(DataRuntime runtime, PrimaryKey origin, PrimaryKey meta)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildDropRun(DataRuntime runtime, PrimaryKey meta, boolean slice)", 37));
        }
        return new ArrayList<>();
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAddRun(DataRuntime runtime, PrimaryKey meta)", 37));
        }
        return new ArrayList<>();
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
        boolean result;
        checkSchema(runtime, meta);
        Map<String, Index> idx = indexs(runtime, meta.getTable());
        if (idx.values().stream().anyMatch(index -> {
            Map<String, Column> indexCols = index.getColumns();
            Collection<String> cols = indexCols.values().stream().map(BaseMetadata::getName).collect(Collectors.toList());
            Map<String, Column> incomingIndexCols = meta.getColumns();
            Collection<String> incomingCols = incomingIndexCols.values().stream().map(BaseMetadata::getName).collect(Collectors.toList());
            return CollectionUtils.isEqualCollection(cols, incomingCols);
        })) {
            log.info("索引已存在，index = {}", meta.getName());
            return false;
        }
        List<Run> runs = buildAddRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
            result = execute(runtime, meta, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            log.info("添加索引信息，table = {},index = {},[执行耗时:{}ms]", meta.getTableName(true), meta.getName(), millis);
        }
        return result;
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
    public boolean drop(DataRuntime runtime, Index meta) throws Exception {
        boolean result = false;
        ACTION.DDL action = ACTION.DDL.INDEX_DROP;
        String random = random(runtime);
        checkSchema(runtime, meta);
        List<Run> runs = buildDropRun(runtime, meta);
        long fr = System.currentTimeMillis();
        try {
//            result = execute(runtime, random, meta, action, runs);
        } finally {
            long millis = System.currentTimeMillis() - fr;
            if (runs.size() > 1) {
                log.info("{}[action:{}][table:{}][name:{}][cmds:{}][result:{}][执行耗时:{}ms]", random, action, meta.getTableName(true), meta.getName(), runs.size(), result, millis);
            }
        }
        return result;
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
        if (log.isDebugEnabled()) {
            log.debug(LogUtil.format("子类(" + this.getClass().getSimpleName() + ")未实现 List<Run> buildAddRun(DataRuntime runtime, Index meta)", 37));
        }
        return new ArrayList<>();
    }

    /**
     * 转换成相应数据库类型
     *
     * @param type type
     * @return String
     */
    @Override
    public ColumnType type(String type) {
        if (null == type) {
            return null;
        }
        boolean array = false;
        if (type.startsWith("_")) {
            type = type.substring(1);
            array = true;
        }
        if (type.endsWith("[]")) {
            type = type.replace("[]", "");
            array = true;
        }
        if (type.contains(" ")) {
            type = type.split(" ")[0];//bigint unsigred
        }
        ColumnType ct = types.get(type.toUpperCase());
        if (null != ct) {
            ct.setArray(array);
        }
        return ct;
    }

    /**
     * 构造完整表名
     *
     * @param builder builder
     * @param meta    BaseMetadata
     * @return StringBuilder
     */
    @Override
    public StringBuilder name(DataRuntime runtime, StringBuilder builder, BaseMetadata<?> meta) {
        Catalog catalog = meta.getCatalog();
        Schema schema = meta.getSchema();
        String name = meta.getName();
        if (com.hwtx.form.util.BasicUtil.isNotEmpty(catalog)) {
            delimiter(builder, catalog).append(".");
        }
        if (com.hwtx.form.util.BasicUtil.isNotEmpty(schema)) {
            delimiter(builder, schema).append(".");
        }
        delimiter(builder, name);
        return builder;
    }

    @Override
    public boolean isBooleanColumn(DataRuntime runtime, Column column) {
        String clazz = column.getClassName();
        if (null != clazz) {
            clazz = clazz.toLowerCase();
            if (clazz.contains("boolean")) {
                return true;
            }
        } else {
            // 如果没有同步法数据库,直接生成column可能只设置了type Name
            String type = column.getTypeName();
            if (null != type) {
                type = type.toLowerCase();
                if (type.equals("bit") || type.equals("bool")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否同数字
     *
     * @param column 列
     * @return boolean
     */
    @Override
    public boolean isNumberColumn(DataRuntime runtime, Column column) {
        String clazz = column.getClassName();
        if (null != clazz) {
            clazz = clazz.toLowerCase();
            if (clazz.startsWith("int") || clazz.contains("integer") || clazz.contains("long") || clazz.contains("decimal") || clazz.contains("float") || clazz.contains("double") || clazz.contains("timestamp")
                    // || clazz.contains("bit")
                    || clazz.contains("short")) {
                return true;
            }
        } else {
            // 如果没有同步法数据库,直接生成column可能只设置了type Name
            String type = column.getTypeName();
            if (null != type) {
                type = type.toLowerCase();
                return type.startsWith("int") || type.contains("float") || type.contains("double") || type.contains("short") || type.contains("long") || type.contains("decimal") || type.contains("numeric") || type.contains("timestamp");
            }
        }
        return false;
    }

    @Override
    public boolean isCharColumn(DataRuntime runtime, Column column) {
        return !isNumberColumn(runtime, column) && !isBooleanColumn(runtime, column);
    }

    /**
     * 先检测rs中是否包含当前key 如果包含再取值, 取值时按keys中的大小写为准
     *
     * @param keys keys
     * @param key  key
     * @param set  ResultSet
     * @return String
     * @throws Exception 异常
     */
    protected String string(Map<String, Integer> keys, String key, ResultSet set, String def) throws Exception {
        Object value = value(keys, key, set);
        if (null != value) {
            return value.toString();
        }
        return def;
    }

    protected String string(Map<String, Integer> keys, String key, ResultSet set) throws Exception {
        return string(keys, key, set, null);
    }

    protected Integer integer(Map<String, Integer> keys, String key, ResultSet set, Integer def) throws Exception {
        Object value = value(keys, key, set);
        if (null != value) {
            return com.hwtx.form.util.BasicUtil.parseInt(value, def);
        }
        return null;
    }

    protected Boolean bool(Map<String, Integer> keys, String key, ResultSet set, Boolean def) throws Exception {
        Object value = value(keys, key, set);
        if (null != value) {
            return com.hwtx.form.util.BasicUtil.parseBoolean(value, def);
        }
        return null;
    }

    protected Boolean bool(Map<String, Integer> keys, String key, ResultSet set, int def) throws Exception {
        Boolean defaultValue = null;
        if (def == 0) {
            defaultValue = false;
        } else if (def == 1) {
            defaultValue = true;
        }
        return bool(keys, key, set, defaultValue);
    }

    /**
     * 从resultset中根据名列取值
     *
     * @param keys 列名位置
     * @param key  列名 多个以,分隔
     * @param set  result
     * @param def  默认值
     * @return Object
     * @throws Exception Exception
     */
    protected Object value(Map<String, Integer> keys, String key, ResultSet set, Object def) throws Exception {
        String[] ks = key.split(",");
        Object result = null;
        for (String k : ks) {
            Integer index = keys.get(k);
            if (null != index && index >= 0) {
                try {
                    // db2 直接用 set.getObject(String) 可能发生 参数无效：未知列名 String
                    result = set.getObject(index);
                    if (null != result) {
                        return result;
                    }
                } catch (Exception e) {

                }
            }
        }
        return def;
    }

    protected Object value(Map<String, Integer> keys, String key, ResultSet set) throws Exception {
        return value(keys, key, set, null);
    }

    /**
     * 写入数据库前类型转换<br/>
     *
     * @param metadata    Column 用来定位数据类型
     * @param placeholder 是否占位符
     * @param value       value
     * @return Object
     */
    @Override
    public Object write(DataRuntime runtime, Column metadata, Object value, boolean placeholder) {
        if (null == value || "NULL".equals(value)) {
            return null;
        }
        Object result = null;
        ColumnType columnType = null;
        DataWriter writer = null;
        boolean isArray = false;
        if (null != metadata) {
            isArray = metadata.isArray();
            //根据列类型
            columnType = metadata.getColumnType();
            if (null != columnType) {
                writer = writer(columnType);
            }
            if (null == writer) {
                String typeName = metadata.getTypeName();
                if (null != typeName) {
                    writer = writer(typeName);
                    if (null != columnType) {
                        writer = writer(type(typeName.toUpperCase()));
                    }
                }
            }
        }
        if (null == columnType) {
            columnType = type(value.getClass().getSimpleName());
        }
        if (null != columnType) {//根据列类型定位writer
            writer = writer(columnType);
        }
        if (null == writer) {//根据值类型定位writer
            writer = writer(value.getClass());
        }
        if (null != writer) {
            result = writer.write(value, placeholder);
        }
        if (null != result) {
            return result;
        }
        if (null != columnType) {
            result = columnType.write(value, null, false);
        }
        if (null != result) {
            return result;
        }
        //根据值类型
        if (!placeholder) {
            if (com.hwtx.form.util.BasicUtil.isNumber(value)) {
                result = value;
            } else {
                result = "'" + value + "'";
            }
        }

        return result;
    }

    @Override
    public String objectName(DataRuntime runtime, String name) {
        KeyAdapter.KEY_CASE keyCase = type().nameCase();
        if (null != keyCase) {
            return keyCase.convert(name);
        }
        return name;
    }


    protected String random(DataRuntime runtime) {
        return "[SQL:" + System.currentTimeMillis() + "-" + com.hwtx.form.util.BasicUtil.getRandomNumberString(8) + "][thread:" + Thread.currentThread().getId() + "][ds:" + runtime.datasource() + "]";
    }

    //A.ID,A.COOE,A.NAME
    protected String concat(String prefix, String split, List<String> columns) {
        StringBuilder builder = new StringBuilder();
        if (com.hwtx.form.util.BasicUtil.isEmpty(prefix)) {
            prefix = "";
        } else {
            if (!prefix.endsWith(".")) {
                prefix += ".";
            }
        }

        boolean first = true;
        for (String column : columns) {
            if (!first) {
                builder.append(split);
            }
            first = false;
            builder.append(prefix).append(column);
        }
        return builder.toString();
    }

    //master.column = data.column
    protected String concatEqual(String master, String data, String split, List<String> columns) {
        StringBuilder builder = new StringBuilder();
        if (com.hwtx.form.util.BasicUtil.isEmpty(master)) {
            master = "";
        } else {
            if (!master.endsWith(".")) {
                master += ".";
            }
        }
        if (com.hwtx.form.util.BasicUtil.isEmpty(data)) {
            data = "";
        } else {
            if (!data.endsWith(".")) {
                data += ".";
            }
        }

        boolean first = true;
        for (String column : columns) {
            if (!first) {
                builder.append(split);
            }
            first = false;
            builder.append(master).append(column).append(" = ").append(data).append(column);
        }
        return builder.toString();
    }

    public StringBuilder delimiter(StringBuilder builder, String src) {
        return SQLUtil.delimiter(builder, src, getDelimiterFr(), getDelimiterTo());
    }

    public StringBuilder delimiter(StringBuilder builder, BaseMetadata<?> src) {
        if (null != src) {
            String name = src.getName();
            if (com.hwtx.form.util.BasicUtil.isNotEmpty(name)) {
                SQLUtil.delimiter(builder, name, getDelimiterFr(), getDelimiterTo());
            }
        }
        return builder;
    }

    protected <T extends BaseMetadata<?>> void fillSchema(T source, T target) {
        Catalog catalog = source.getCatalog();
        Schema schema = source.getSchema();
        if (com.hwtx.form.util.BasicUtil.isNotEmpty(catalog)) {
            target.setCatalog(catalog);
        }
        if (BasicUtil.isNotEmpty(schema)) {
            target.setSchema(schema);
        }
    }

}