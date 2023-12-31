package com.hwtx.form.domain.ds;

import com.hwtx.form.domain.ds.mysql.MySQLGenusAdapter;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DefaultDao {
    private final DataRuntime runtime;

    DefaultDao(JdbcTemplate jdbcTemplate) {
        runtime = new DataRuntime(jdbcTemplate, new MySQLGenusAdapter());
    }


    public DataRuntime runtime() {
        return runtime;
    }

//    /**
//     * 查询<br/>
//     * 注意:如果设置了自动还原,querys会自动还原数据源(dao内部执行过程中不要调用除非是一些重载),而select不会
//     *
//     * @param prepare    构建最终执行命令的全部参数，包含表（或视图｜函数｜自定义SQL)查询条件 排序 分页等
//     * @param configs    过滤条件及相关配置
//     * @param conditions 简单过滤条件
//     * @return DataSet
//     */
//    
//    public DataSet querys(DataRuntime runtime, String random, RunPrepare prepare, ConfigStore configs, String... conditions) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().querys(runtime, null, prepare, configs, conditions);
//
//    }

    public boolean exists(Table table) {
        return runtime.getAdapter().exists(runtime(), table);
    }

    public boolean create(Table table) throws Exception {
        return runtime.getAdapter().create(runtime(), table);
    }

    public boolean alter(Table table, Column column) throws Exception {
        return runtime.getAdapter().alter(runtime, table, column);
    }

//    /**
//     * 查询
//     *
//     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
//     * @param random  用来标记同一组命令
//     * @param table   查询表结构时使用
//     * @param system  系统表不查询表结构
//     * @param run     最终待执行的命令和参数(如果是JDBC环境就是SQL)
//     * @return DataSet
//     */
//    protected DataSet select(DataRuntime runtime, String random, boolean system, String table, ConfigStore configs, Run run) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().select(runtime, random, system, table, configs, run);
//
//    }
//
//    /**
//     * 执行
//     *
//     * @param runtime    运行环境主要包含驱动适配器 数据源或客户端
//     * @param random     用来标记同一组命令
//     * @param prepare    构建最终执行命令的全部参数，包含表（或视图｜函数｜自定义SQL)查询条件 排序 分页等
//     * @param configs    configs
//     * @param conditions conditions
//     * @return 影响行数
//     */
//    
//    public long execute(DataRuntime runtime, String random, RunPrepare prepare, ConfigStore configs, String... conditions) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().execute(runtime, random, prepare, configs, conditions);
//
//    }
//
//    
//    public long execute(DataRuntime runtime, String random, int batch, String sql, List<Object> values) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().execute(runtime, random, batch, null, sql, values);
//    }
//
//    /**
//     * 执行存储过程
//     *
//     * @param runtime   运行环境主要包含驱动适配器 数据源或客户端
//     * @param random    用来标记同一组命令
//     * @param procedure 存储过程
//     * @return 是否成功
//     */
//    
//    public boolean execute(DataRuntime runtime, String random, Procedure procedure) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().execute(runtime, random, procedure);
//    }
//
//    /**
//     * 根据存储过程查询(MSSQL AS 后必须加 SET NOCOUNT ON)<br/>
//     *
//     * @param procedure procedure
//     * @param navi      navi
//     * @return DataSet
//     */
//    
//    public DataSet querys(DataRuntime runtime, String random, Procedure procedure, PageNavi navi) {
//        if (null == runtime) {
//            runtime = runtime();
//        }
//        return runtime.getAdapter().querys(runtime, random, procedure, navi);
//    }

    /* *****************************************************************************************************************
     *
     * 													metadata
     *
     * =================================================================================================================
     * database			: 数据库
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


//    /**
//     * 根据sql获取列结构,如果有表名应该调用metadata().columns(table);或metadata().table(table).getColumns()
//     *
//     * @param prepare RunPrepare
//     * @return LinkedHashMap
//     */
//    public LinkedHashMap<String, Column> metadata(RunPrepare prepare, boolean comment) {
//        DataRuntime runtime = runtime();
//        return runtime.getAdapter().metadata(runtime, prepare, comment);
//    }


    public LinkedHashMap<String, Catalog> catalogs(DataRuntime runtime, String random, String name) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().catalogs(runtime, random, name);
    }


    public List<Catalog> catalogs(DataRuntime runtime, String random, boolean greedy, String name) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().catalogs(runtime, random, greedy, name);
    }


    public LinkedHashMap<String, Schema> schemas(DataRuntime runtime, String random, Catalog catalog, String name) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().schemas(runtime, random, catalog, name);
    }


    public List<Schema> schemas(DataRuntime runtime, String random, boolean greedy, Catalog catalog, String name) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().schemas(runtime, random, greedy, catalog, name);
    }

    /* *****************************************************************************************************************
     * 													column
     * -----------------------------------------------------------------------------------------------------------------
     * LinkedHashMap<String, Column> columns(Table table)
     * LinkedHashMap<String, Column> columns(String table)
     * LinkedHashMap<String, Column> columns(Catalog catalog, Schema schema, String table)
     ******************************************************************************************************************/

    public <T extends Column> LinkedHashMap<String, T> columns(DataRuntime runtime, String random, boolean greedy, Table table, boolean primary) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().columns(runtime, random, greedy, table, primary);
    }


    public <T extends Column> List<T> columns(DataRuntime runtime, String random, boolean greedy, Catalog catalog, Schema schema) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().columns(runtime, random, greedy, catalog, schema);

    }

    /* *****************************************************************************************************************
     * 													primary
     * -----------------------------------------------------------------------------------------------------------------
     * PrimaryKey primary(Table table)
     * PrimaryKey primary(String table)
     * PrimaryKey primary(Catalog catalog, Schema schema, String table)
     ******************************************************************************************************************/

    public Map<String, Index> indexs(Table table) {
        return runtime.getAdapter().indexs(runtime(), table);
    }

    public boolean drop(Table meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().drop(runtime, meta);
    }

    /**
     * 重命名
     *
     * @param origin 原表
     * @param name   新名称
     * @return boolean
     * @throws Exception DDL异常
     */

    public boolean rename(Table origin, String name) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().rename(runtime, origin, name);
    }

    public boolean add(Index meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().add(runtime, meta);
    }


    public boolean drop(Column meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().drop(runtime, meta);
    }

    public boolean rename(Column origin, String name) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().rename(runtime, origin, name);
    }


    /* *****************************************************************************************************************
     * 													primary
     * -----------------------------------------------------------------------------------------------------------------
     * boolean add(PrimaryKey primary) throws Exception
     * boolean alter(PrimaryKey primary) throws Exception
     * boolean drop(PrimaryKey primary) throws Exception
     ******************************************************************************************************************/

    public boolean add(PrimaryKey meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().add(runtime, meta);
    }

    public boolean drop(PrimaryKey meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().drop(runtime, meta);
    }


    public boolean rename(PrimaryKey origin, String name) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().rename(runtime, origin, name);
    }

    public boolean alter(Table table) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().alter(runtime, table);
    }


    public boolean drop(Index meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().drop(runtime, meta);
    }
}
