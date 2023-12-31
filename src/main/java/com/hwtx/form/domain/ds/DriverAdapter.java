package com.hwtx.form.domain.ds;

import org.anyline.adapter.DataReader;
import org.anyline.adapter.DataWriter;
import org.anyline.data.adapter.DataReaderFactory;
import org.anyline.data.adapter.DataWriterFactory;
import org.anyline.data.adapter.SystemDataReaderFactory;
import org.anyline.data.adapter.SystemDataWriterFactory;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.run.RunValue;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.metadata.*;
import org.anyline.metadata.type.ColumnType;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.util.BasicUtil;

import java.util.*;

/**
 * DriverAdapter主要用来构造和执行不同数据库的命令,一般会分成3步,以insert为例<br/>
 * 1.insert[调用入口]提供为上一步调用的方法,方法内部再调用[命令合成]生成具体命令，最后调用[命令执行]执行命令<br/>
 * 2.insert[命令合成]根据不同的数据库生成具体的insert命令<br/>
 * 3.insert[命令执行]执行[命令合成]生成的命令<br/>
 * 其中[调用入口],[命令执行]大部分通用，重点是[命令合成]需要由每个数据库的适配器各自生成<br/>
 * [命令执行]过程注意数据库是否支持占位符，是否支持返回自增值，是否支持批量量插入<br/>
 * 以上3步在子类中要全部实现，如果不实现，需要输出日志或调用super方法(用于异常堆栈输出)<br/>
 */
public interface DriverAdapter {

    boolean exists(DataRuntime runtime, Table table);

    boolean alter(DataRuntime runtime, Table table, Column meta) throws Exception;

    default boolean execute(DataRuntime runtime, BaseMetadata meta, List<Run> runs) {
        boolean result = true;
        for (Run run : runs) {
            result = update(runtime, run) >= 0 && result;
        }
        return result;
    }

    long update(DataRuntime runtime, Run run);

    // 内置VALUE
    enum SQL_BUILD_IN_VALUE {
        CURRENT_DATE("CURRENT_DATE", "当前日期"), CURRENT_TIME("CURRENT_TIME", "当前时间"), CURRENT_TIMESTAMP("CURRENT_TIMESTAMP", "当前时间戳");
        private final String code;
        private final String name;

        SQL_BUILD_IN_VALUE(String code, String name) {
            this.code = code;
            this.name = name;
        }

        String getCode() {
            return code;
        }

        String getName() {
            return name;
        }
    }

    /**
     * 数据库类型
     *
     * @return DatabaseType
     */
    DatabaseType type();

    /**
     * 支持的数据库版本
     *
     * @return String
     */
    String version();

    /**
     * @param feature  当前运行环境特征
     * @param keywords 关键字+jdbc-url前缀+驱动类
     * @return 数据源特征中包含上以任何一项都可以通过
     */
    default boolean match(String feature, List<String> keywords) {
        feature = feature.toLowerCase();
        if (null != keywords) {
            for (String k : keywords) {
                if (BasicUtil.isEmpty(k)) {
                    continue;
                }
                if (feature.contains(k)) {
                    return true;
                }
            }
        }
        return false;
    }

    String TAB = "\t";
    String BR = "\n";
    String BR_TAB = "\n\t";


    /**
     * 界定符(分隔符)
     *
     * @return String
     */
    String getDelimiterFr();

    String getDelimiterTo();

    /**
     * 对应的兼容模式，有些数据库会兼容oracle或pg,需要分别提供两个JDBCAdapter或者直接依赖oracle/pg的adapter
     * 参考DefaultJDBCAdapterUtil定位adapter的方法
     *
     * @return DatabaseType
     */
    DatabaseType compatible();

    /**
     * 转换成相应数据库支持类型
     *
     * @param type type
     * @return ColumnType
     */
    ColumnType type(String type);

    /**
     * 写入数据库前 类型转换
     *
     * @param supports 写入的原始类型 class ColumnType StringColumnType
     * @param writer   DataWriter
     */
    default void reg(Object[] supports, DataWriter writer) {
        SystemDataWriterFactory.reg(type(), supports, writer);
    }

    /**
     * 写入数据库时 类型转换 写入的原始类型需要writer中实现supports
     *
     * @param writer DataWriter
     */
    default void reg(DataWriter writer) {
        SystemDataWriterFactory.reg(type(), null, writer);
    }

    /**
     * 读取数据库入 类型转换
     *
     * @param supports 读取的原始类型 class ColumnType StringColumnType
     * @param reader   DataReader
     */
    default void reg(Object[] supports, DataReader reader) {
        SystemDataReaderFactory.reg(type(), supports, reader);
    }

    /**
     * 读取数据库入 类型转换 读取的原始类型需要reader中实现supports
     *
     * @param reader DataReader
     */
    default void reg(DataReader reader) {
        SystemDataReaderFactory.reg(type(), null, reader);
    }

    /**
     * 根据读出的数据类型 定位DataReader
     *
     * @param type class ColumnType StringColumnType
     * @return DataReader
     */
    default DataReader reader(Object type) {
        DataReader reader = DataReaderFactory.reader(type(), type);
        if (null == reader) {
            reader = SystemDataReaderFactory.reader(type(), type);
        }
        if (null == reader) {
            reader = DataReaderFactory.reader(DatabaseType.NONE, type);
        }
        if (null == reader) {
            reader = SystemDataReaderFactory.reader(DatabaseType.NONE, type);
        }
        return reader;
    }

    /**
     * 根据写入的数据类型 定位DataWriter
     *
     * @param type class ColumnType StringColumnType
     * @return DataWriter
     */
    default DataWriter writer(Object type) {
        DataWriter writer = DataWriterFactory.writer(type(), type);
        if (null == writer) {
            writer = SystemDataWriterFactory.writer(type(), type);
        }
        if (null == writer) {
            writer = DataWriterFactory.writer(DatabaseType.NONE, type);
        }
        if (null == writer) {
            writer = SystemDataWriterFactory.writer(DatabaseType.NONE, type);
        }
        return writer;
    }

    /* *****************************************************************************************************************
     *
     * 													metadata
     *
     * =================================================================================================================
     * database			: 数据库(catalog, schema)
     * table			: 表
     * master table		: 主表
     * partition table	: 分区表
     * column			: 列
     * tag				: 标签
     * primary key      : 主键
     * foreign key		: 外键
     * index			: 索引
     * constraint		: 约束
     * trigger		    : 触发器
     * procedure        : 存储过程
     * function         : 函数
     ******************************************************************************************************************/

    /**
     * 根据运行环境识别 catalog与schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    BaseMetadata
     * @param <T>     BaseMetadata
     */
    <T extends BaseMetadata> void checkSchema(DataRuntime runtime, T meta);

    default <T extends BaseMetadata> void checkSchema(T meta, String catalog, String schema) {
        if (BasicUtil.isEmpty(meta.getCatalogName())) {
            meta.setCatalog(catalog);
        }
        if (BasicUtil.isEmpty(meta.getSchemaName())) {
            meta.setSchema(schema);
        }
    }

    /**
     * 在调用jdbc接口前处理业务中的catalog,schema,部分数据库(如mysql)业务系统与dbc标准可能不一致根据实际情况处理<br/>
     *
     * @param catalog catalog
     * @param schema  schema
     * @return String[]
     */
    default String[] checkSchema(String catalog, String schema) {
        return new String[]{schema, null};
    }
    /* *****************************************************************************************************************
     * 													catalog
     ******************************************************************************************************************/

    /**
     * catalog[调用入口]
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param random  用来标记同一组命令
     * @param name    名称统配符或正则
     * @return LinkedHashMap
     */
    LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, String random, String name);

    List<Catalog> catalogs(DataRuntime runtime, String random, boolean greedy, String name);

    default Catalog catalog(DataRuntime runtime, String random, String name) {
        List<Catalog> catalogs = catalogs(runtime, random, false, name);
        if (!catalogs.isEmpty()) {
            return catalogs.get(0);
        }
        return null;
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
    List<Run> buildQueryCatalogRun(DataRuntime runtime, boolean greedy, String name) throws Exception;

    default List<Run> buildQueryCatalogRun(DataRuntime runtime) throws Exception {
        return buildQueryCatalogRun(runtime, false, null);
    }

    /**
     * catalog[结果集封装]<br/>
     * 根据查询结果集构造 catalog
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param index    第几条SQL 对照 buildQueryDatabaseRun 返回顺序
     * @param create   上一步没有查到的,这一步是否需要新创建
     * @param catalogs 上一步查询结果
     * @param set      查询结果集
     * @return databases
     * @throws Exception 异常
     */
    LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, int index, boolean create, LinkedHashMap<String, Catalog> catalogs, DataSet set) throws Exception;

    List<Catalog> catalogs(DataRuntime runtime, int index, boolean create, List<Catalog> catalogs, DataSet set) throws Exception;

    /**
     * catalog[结果集封装]<br/>
     * 根据驱动内置接口补充 catalog
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param create   上一步没有查到的,这一步是否需要新创建
     * @param catalogs 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, boolean create, LinkedHashMap<String, Catalog> catalogs) throws Exception;

    /**
     * catalog[结果集封装]<br/>
     * 根据驱动内置接口补充 catalog
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param create   上一步没有查到的,这一步是否需要新创建
     * @param catalogs 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    List<Catalog> catalogs(DataRuntime runtime, boolean create, List<Catalog> catalogs) throws Exception;

    Catalog catalog(DataRuntime runtime, int index, boolean create, DataSet set) throws Exception;

    /* *****************************************************************************************************************
     * 													schema
     ******************************************************************************************************************/

    /**
     * schema[调用入口]
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param random  用来标记同一组命令
     * @param catalog catalog
     * @param name    名称统配符或正则
     * @return LinkedHashMap
     */
    LinkedHashMap<String, Schema> schemas(DataRuntime runtime, String random, Catalog catalog, String name);

    default LinkedHashMap<String, Schema> schemas(DataRuntime runtime, String random, String name) {
        return schemas(runtime, random, null, name);
    }

    List<Schema> schemas(DataRuntime runtime, String random, boolean greedy, Catalog catalog, String name);

    default List<Schema> schemas(DataRuntime runtime, String random, boolean greedy, String name) {
        return schemas(runtime, random, greedy, null, name);
    }

    default Schema schema(DataRuntime runtime, String random, Catalog catalog, String name) {
        List<Schema> schemas = schemas(runtime, random, false, catalog, name);
        if (!schemas.isEmpty()) {
            return schemas.get(0);
        }
        return null;
    }

    /**
     * schema[命令合成]<br/>
     * 查询所有数据库
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param catalog catalog
     * @param name    名称统配符或正则
     * @param greedy  贪婪模式 true:查询权限范围内尽可能多的数据
     * @return sqls
     * @throws Exception 异常
     */
    List<Run> buildQuerySchemaRun(DataRuntime runtime, boolean greedy, Catalog catalog, String name) throws Exception;

    default List<Run> buildQuerySchemaRun(DataRuntime runtime, String name) throws Exception {
        return buildQuerySchemaRun(runtime, false, null, name);
    }

    default List<Run> buildQuerySchemaRun(DataRuntime runtime, Catalog catalog) throws Exception {
        return buildQuerySchemaRun(runtime, false, catalog, null);
    }

    /**
     * schema[结果集封装]<br/>
     * 根据查询结果集构造 schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照 buildQueryDatabaseRun 返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param schemas 上一步查询结果
     * @param set     查询结果集
     * @return databases
     * @throws Exception 异常
     */
    LinkedHashMap<String, Schema> schemas(DataRuntime runtime, int index, boolean create, LinkedHashMap<String, Schema> schemas, DataSet set) throws Exception;

    List<Schema> schemas(DataRuntime runtime, int index, boolean create, List<Schema> schemas, DataSet set) throws Exception;


    /**
     * schema[结果集封装]<br/>
     * 根据驱动内置接口补充 schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param schemas 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    LinkedHashMap<String, Schema> schemas(DataRuntime runtime, boolean create, LinkedHashMap<String, Schema> schemas) throws Exception;

    /**
     * schema[结果集封装]<br/>
     * 根据驱动内置接口补充 schema
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param schemas 上一步查询结果
     * @return databases
     * @throws Exception 异常
     */
    List<Schema> schemas(DataRuntime runtime, boolean create, List<Schema> schemas) throws Exception;

    Schema schema(DataRuntime runtime, int index, boolean create, DataSet set) throws Exception;

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
    List<Run> buildQueryTablesRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types) throws Exception;

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
    List<Run> buildQueryTableCommentRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types) throws Exception;

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
    <T extends Table> LinkedHashMap<String, T> comments(DataRuntime runtime, int index, boolean create, Catalog catalog, Schema schema, LinkedHashMap<String, T> tables, DataSet set) throws Exception;

    /**
     * table[命令合成]<br/>
     * 查询表DDL
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return List
     */
    List<Run> buildQueryDDLRun(DataRuntime runtime, Table table) throws Exception;

    /**
     * table[结果集封装]<br/>
     * 查询表DDL
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照 buildQueryDDLRun 返回顺序
     * @param table   表
     * @param ddls    上一步查询结果
     * @param set     sql执行的结果集
     * @return List
     */
    List<String> ddl(DataRuntime runtime, int index, Table table, List<String> ddls, DataSet set);

    /**
     * column[调用入口](多方法合成)<br/>
     * 查询表结构
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param random  用来标记同一组命令
     * @param greedy  贪婪模式 true:如果不填写catalog或schema则查询全部 false:只在当前catalog和schema中查询
     * @param table   表 如果不提供表名则根据data解析,表名可以事实前缀&lt;数据源名&gt;表示切换数据源
     * @param primary 是否检测主键
     * @param <T>     Column
     * @return Column
     */
    <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, String random, boolean greedy, Table table, boolean primary);

    /**
     * column[调用入口](方法1)<br/>
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
    <T extends Column> List<T> columns(DataRuntime runtime, String random, boolean greedy, Catalog catalog, Schema schema, String table);

    default <T extends Column> List<T> columns(DataRuntime runtime, String random, boolean greedy, Catalog catalog, Schema schema) {
        return columns(runtime, random, greedy, catalog, schema, null);
    }

    /**
     * column[调用入口](方法3)<br/>
     * DatabaseMetaData
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return pattern 列名称通配符
     * @throws Exception 异常
     */
    HashMap<String, Column> columns(DataRuntime runtime, Table table, String pattern) throws Exception;


    /**
     * column[命令合成](方法1)<br/>
     * 查询表上的列
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param table    表
     * @param metadata 是否根据metadata(true:SELECT * FROM T WHERE 1=0,false:查询系统表)
     * @return sqls
     */
    List<Run> buildQueryColumnRun(DataRuntime runtime, Table table, boolean metadata) throws Exception;

    /**
     * column[结果集封装](方法1)<br/>
     * 根据系统表查询SQL获取表结构
     * 根据查询结果集构造Column
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照 buildQueryColumnRun返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param table   表
     * @param columns 上一步查询结果
     * @param set     系统表查询SQL结果集
     * @return columns
     * @throws Exception 异常
     */
    <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, int index, boolean create, Table table, LinkedHashMap<String, T> columns, DataSet set) throws Exception;

    /**
     * column[结果集封装](方法1)<br/>
     * 根据系统表查询SQL获取表结构
     * 根据查询结果集构造Column
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条SQL 对照 buildQueryColumnRun返回顺序
     * @param create  上一步没有查到的,这一步是否需要新创建
     * @param table   表
     * @param columns 上一步查询结果
     * @param set     系统表查询SQL结果集
     * @return columns
     * @throws Exception 异常
     */
    <T extends Column> List<T> columns(DataRuntime runtime, int index, boolean create, Table table, List<T> columns, DataSet set) throws Exception;
    /* *****************************************************************************************************************
     * 													primary
     ******************************************************************************************************************/

    PrimaryKey primary(DataRuntime runtime, Table table);

    /**
     * primary[命令合成]<br/>
     * 查询表上的主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sqls
     */
    List<Run> buildQueryPrimaryRun(DataRuntime runtime, Table table) throws Exception;

    /**
     * primary[结构集封装]<br/>
     * 根据查询结果集构造PrimaryKey
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param index   第几条查询SQL 对照 buildQueryIndexRun 返回顺序
     * @param table   表
     * @param set     sql查询结果
     * @throws Exception 异常
     */
    PrimaryKey primary(DataRuntime runtime, int index, Table table, DataSet set) throws Exception;

    /**
     * index[命令合成]<br/>
     * 查询表上的索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param name    名称
     * @return sqls
     */
    List<Run> buildQueryIndexRun(DataRuntime runtime, Table table, String name);

    Map<String, Index> indexs(DataRuntime runtime, Table table);

    /**
     * table[调用入口]<br/>
     * 创建表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    boolean create(DataRuntime runtime, Table meta) throws Exception;

    /**
     * table[调用入口]<br/>
     * 修改表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    boolean alter(DataRuntime runtime, Table meta) throws Exception;

    /**
     * table[调用入口]<br/>
     * 删除表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    boolean drop(DataRuntime runtime, Table meta) throws Exception;

    /**
     * table[调用入口]<br/>
     * 重命名表,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param origin  原表
     * @param name    新名称
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    boolean rename(DataRuntime runtime, Table origin, String name) throws Exception;


    /**
     * table[命令合成-子流程]<br/>
     * 部分数据库在创建主表时用主表关键字(默认)，部分数据库普通表主表子表都用table，部分数据库用collection、timeseries等
     *
     * @param table 表
     * @return String
     */
    default String keyword(Table table) {
        return table.getKeyword();
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
     * @param table   表
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildCreateRun(DataRuntime runtime, Table table) throws Exception;

    /**
     * table[命令合成]<br/>
     * 修改列
     * 有可能生成多条SQL,根据数据库类型优先合并成一条执行
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param columns 列
     * @return List
     */
    List<Run> buildAlterRun(DataRuntime runtime, Table table, Collection<Column> columns) throws Exception;

    /**
     * table[命令合成]<br/>
     * 重命名
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildRenameRun(DataRuntime runtime, Table table) throws Exception;

    /**
     * table[命令合成]<br/>
     * 删除表
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildDropRun(DataRuntime runtime, Table table) throws Exception;


    /**
     * table[命令合成-子流程]<br/>
     * 添加表备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildAppendCommentRun(DataRuntime runtime, Table table) throws Exception;

    /**
     * table[命令合成-子流程]<br/>
     * 修改备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildChangeCommentRun(DataRuntime runtime, Table table) throws Exception;


    /**
     * table[命令合成-子流程]<br/>
     * 定义表的主键标识,在创建表的DDL结尾部分(注意不要跟列定义中的主键重复)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param table   表
     * @return StringBuilder
     */
    StringBuilder primary(DataRuntime runtime, StringBuilder builder, Table table);


    /**
     * table[命令合成-子流程]<br/>
     * 编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param table   表
     * @return StringBuilder
     */
    StringBuilder charset(DataRuntime runtime, StringBuilder builder, Table table);


    /**
     * table[命令合成-子流程]<br/>
     * 表备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param table   表
     * @return StringBuilder
     */
    StringBuilder comment(DataRuntime runtime, StringBuilder builder, Table table);

    /**
     * column[调用入口]<br/>
     * 删除列,执行的SQL通过meta.ddls()返回
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return boolean 是否执行成功
     * @throws Exception DDL异常
     */
    boolean drop(DataRuntime runtime, Column meta) throws Exception;

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
    boolean rename(DataRuntime runtime, Column origin, String name) throws Exception;


    /**
     * column[命令合成]<br/>
     * 添加列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    List<Run> buildAddRun(DataRuntime runtime, Column column, boolean slice) throws Exception;

    default List<Run> buildAddRun(DataRuntime runtime, Column column) throws Exception {
        return buildAddRun(runtime, column, false);
    }

    /**
     * column[命令合成]<br/>
     * 修改列
     * 有可能生成多条SQL
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return List
     */
    List<Run> buildAlterRun(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成]<br/>
     * 删除列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    List<Run> buildDropRun(DataRuntime runtime, Column column, boolean slice) throws Exception;

    default List<Run> buildDropRun(DataRuntime runtime, Column column) throws Exception {
        return buildDropRun(runtime, column, false);
    }

    /**
     * column[命令合成]<br/>
     * 修改列名
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return String
     */
    List<Run> buildRenameRun(DataRuntime runtime, Column column) throws Exception;


    /**
     * column[命令合成-子流程]<br/>
     * 修改数据类型
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return String
     */
    List<Run> buildChangeTypeRun(DataRuntime runtime, Column column) throws Exception;


    /**
     * column[命令合成-子流程]<br/>
     * 修改表的关键字
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @return String
     */
    String alterColumnKeyword(DataRuntime runtime);

    /**
     * column[命令合成-子流程]<br/>
     * 添加列引导<br/>
     * alter table sso_user [add column] type_code int
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder StringBuilder
     * @param column  列
     * @return String
     */
    StringBuilder addColumnGuide(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 删除列引导<br/>
     * alter table sso_user [drop column] type_code
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder StringBuilder
     * @param column  列
     * @return String
     */
    StringBuilder dropColumnGuide(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 修改默认值
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return String
     */
    List<Run> buildChangeDefaultRun(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成-子流程]<br/>
     * 修改非空限制
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return String
     */
    List<Run> buildChangeNullableRun(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成-子流程]<br/>
     * 修改备注
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return String
     */
    List<Run> buildChangeCommentRun(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成-子流程]<br/>
     * 添加表备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildAppendCommentRun(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成-子流程]<br/>
     * 取消自增
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return sql
     * @throws Exception 异常
     */
    List<Run> buildDropAutoIncrement(DataRuntime runtime, Column column) throws Exception;

    /**
     * column[命令合成-子流程]<br/>
     * 定义列，依次拼接下面几个属性注意不同数据库可能顺序不一样
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder define(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:创建或删除列之前  检测表是否存在
     * IF NOT EXISTS
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param exists  exists
     * @return StringBuilder
     */
    StringBuilder checkColumnExists(DataRuntime runtime, StringBuilder builder, boolean exists);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:数据类型
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder type(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:列数据类型定义
     *
     * @param runtime           运行环境主要包含驱动适配器 数据源或客户端
     * @param builder           builder
     * @param column            列
     * @param type              数据类型(已经过转换)
     * @param isIgnorePrecision 是否忽略长度
     * @param isIgnoreScale     是否忽略小数
     * @return StringBuilder
     */
    StringBuilder type(DataRuntime runtime, StringBuilder builder, Column column, String type, boolean isIgnorePrecision, boolean isIgnoreScale);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:是否忽略长度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return boolean
     */
    boolean isIgnorePrecision(DataRuntime runtime, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:是否忽略精度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return boolean
     */
    boolean isIgnoreScale(DataRuntime runtime, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:是否忽略长度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return boolean
     */
    Boolean checkIgnorePrecision(DataRuntime runtime, String type);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:是否忽略精度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return boolean
     */
    Boolean checkIgnoreScale(DataRuntime runtime, String type);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:非空
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder nullable(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder charset(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:默认值
     *
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder defaultValue(DataRuntime runtime, StringBuilder builder, Column column);


    /**
     * column[命令合成-子流程]<br/>
     * 定义列的主键标识(注意不要跟表定义中的主键重复)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder primary(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:递增列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder increment(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:更新行事件
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder onupdate(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * column[命令合成-子流程]<br/>
     * 定义列:备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param column  列
     * @return StringBuilder
     */
    StringBuilder comment(DataRuntime runtime, StringBuilder builder, Column column);

    /**
     * primary[调用入口]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    boolean add(DataRuntime runtime, PrimaryKey meta) throws Exception;

    boolean add(DataRuntime runtime, Index meta) throws Exception;

    /**
     * primary[调用入口]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @return 是否执行成功
     * @throws Exception 异常
     */
    boolean drop(DataRuntime runtime, PrimaryKey meta) throws Exception;

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
    boolean rename(DataRuntime runtime, PrimaryKey origin, String name) throws Exception;

    /**
     * primary[命令合成]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param primary 主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    List<Run> buildAddRun(DataRuntime runtime, PrimaryKey primary, boolean slice) throws Exception;

    /**
     * primary[命令合成]<br/>
     * 创建完表后，添加主键，与列主键标识，表主键标识三选一<br/>
     * 大部分情况调用buildAddRun<br/>
     * 默认不调用，大部分数据库在创建列或表时可以直接标识出主键<br/>
     * 只有在创建表过程中不支持创建主键的才需要实现这个方法
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param primary 主键
     * @return String
     */
    default List<Run> buildAddRunAfterTable(DataRuntime runtime, PrimaryKey primary) throws Exception {
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
    List<Run> buildAlterRun(DataRuntime runtime, PrimaryKey origin, PrimaryKey meta) throws Exception;

    default List<Run> buildAlterRun(DataRuntime runtime, PrimaryKey meta) throws Exception {
        return buildAlterRun(runtime, null, meta);
    }

    /**
     * primary[命令合成]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param primary 主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    List<Run> buildDropRun(DataRuntime runtime, PrimaryKey primary, boolean slice) throws Exception;

    /**
     * primary[命令合成]<br/>
     * 修改主键名
     * 一般不直接调用,如果需要由buildAlterRun内部统一调用
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param primary 主键
     * @return String
     */
    List<Run> buildRenameRun(DataRuntime runtime, PrimaryKey primary) throws Exception;

    /**
     * index[调用入口]<br/>
     * 删除索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return 是否执行成功
     * @throws Exception 异常
     */
    boolean drop(DataRuntime runtime, Index meta) throws Exception;


    /**
     * index[命令合成]<br/>
     * 添加索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return String
     */
    List<Run> buildAddRun(DataRuntime runtime, Index meta) throws Exception;

    /**
     * index[命令合成]<br/>
     * 删除索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return String
     */
    List<Run> buildDropRun(DataRuntime runtime, Index meta) throws Exception;

    /**
     * index[命令合成-子流程]<br/>
     * 索引类型
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @param builder builder
     * @return StringBuilder
     */
    StringBuilder type(DataRuntime runtime, StringBuilder builder, Index meta);

    /**
     * index[命令合成-子流程]<br/>
     * 索引备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @param builder builder
     * @return StringBuilder
     */
    StringBuilder comment(DataRuntime runtime, StringBuilder builder, Index meta);

    /* *****************************************************************************************************************
     *
     * 													common
     *
     ******************************************************************************************************************/
    StringBuilder name(DataRuntime runtime, StringBuilder builder, BaseMetadata table);

    /**
     * 获取单主键列名
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param obj     obj
     * @return String
     */
    String getPrimaryKey(DataRuntime runtime, Object obj);

    /**
     * 获取单主键值
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param obj     obj
     * @return Object
     */
    Object getPrimaryValue(DataRuntime runtime, Object obj);
    /*
     */

    /**
     * 数据类型转换
     * 子类先解析(有些同名的类型以子类为准)、失败后再调用默认转换
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param catalog catalog
     * @param schema  schema
     * @param table   表
     * @param run     值
     * @return boolean 返回false表示转换失败 如果有多个 adapter 则交给adapter继续转换
     */
    boolean convert(DataRuntime runtime, Catalog catalog, Schema schema, String table, RunValue run);

    boolean convert(DataRuntime runtime, Table table, Run run);

    boolean convert(DataRuntime runtime, ConfigStore configs, Run run);

    /**
     * 数据类型转换
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param columns 列
     * @param run     值
     * @return boolean 返回false表示转换失败 如果有多个adapter 则交给adapter继续转换
     */
    boolean convert(DataRuntime runtime, Map<String, Column> columns, RunValue run);

    /**
     * 数据类型转换,没有提供column的根据value类型
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @param run     值
     * @return boolean 返回false表示转换失败 如果有多个adapter 则交给adapter继续转换
     */
    boolean convert(DataRuntime runtime, Column column, RunValue run);

    Object convert(DataRuntime runtime, Column column, Object value);

    /**
     * 在不检测数据库结构时才生效,否则会被convert代替
     * 生成value格式 主要确定是否需要单引号  或  类型转换
     * 有些数据库不提供默认的 隐式转换 需要显示的把String转换成相应的数据类型
     * 如 TO_DATE('')
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param row     DataRow 或 Entity
     * @param key     列名
     */
    void value(DataRuntime runtime, StringBuilder builder, Object row, String key);

    /**
     * 根据数据类型生成SQL(如是否需要'',是否需要格式转换函数)
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param value value
     */
    //void format(StringBuilder builder, Object value);

    /**
     * 从数据库中读取数据,常用的基本类型可以自动转换,不常用的如json/point/polygon/blob等转换成anyline对应的类型
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param metadata Column 用来定位数据类型
     * @param value    value
     * @param clazz    目标数据类型(给entity赋值时可以根据class, DataRow赋值时可以指定class，否则按检测metadata类型转换 转换不不了的原样返回)
     * @return Object
     */
    Object read(DataRuntime runtime, Column metadata, Object value, Class clazz);

    /**
     * 通过占位符写入数据库前转换成数据库可接受的Java数据类型<br/>
     *
     * @param runtime     运行环境主要包含驱动适配器 数据源或客户端
     * @param metadata    Column 用来定位数据类型
     * @param placeholder 是否占位符
     * @param value       value
     * @return Object
     */
    Object write(DataRuntime runtime, Column metadata, Object value, boolean placeholder);

    /**
     * 拼接字符串
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param args    args
     * @return String
     */
    String concat(DataRuntime runtime, String... args);

    /**
     * 是否是数字列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return boolean
     */
    boolean isNumberColumn(DataRuntime runtime, Column column);

    /**
     * 是否是boolean列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return boolean
     */
    boolean isBooleanColumn(DataRuntime runtime, Column column);

    /**
     * 是否是字符类型
     * 决定值是否需要加单引号
     * number boolean 返回false
     * 其他返回true
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param column  列
     * @return boolean
     */
    boolean isCharColumn(DataRuntime runtime, Column column);

    void addRunValue(DataRuntime runtime, Run run, Compare compare, Column column, Object value);

    /**
     * 对象名称格式化(大小写转换)，在查询系统表时需要
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param name    name
     * @return String
     */
    String objectName(DataRuntime runtime, String name);
}