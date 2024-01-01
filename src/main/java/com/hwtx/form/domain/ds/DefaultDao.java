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

    public boolean exists(Table table) {
        return runtime.getAdapter().exists(runtime(), table);
    }

    public boolean create(Table table) throws Exception {
        return runtime.getAdapter().create(runtime(), table);
    }

    public boolean alter(Table table, Column column) throws Exception {
        return runtime.getAdapter().alter(runtime, table, column);
    }

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
