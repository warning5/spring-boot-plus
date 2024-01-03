package com.hwtx.form.domain.ds;

import com.hwtx.form.domain.ds.metadata.*;
import com.hwtx.form.domain.ds.mysql.MySQLGenusAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

    public <T extends Column> List<T> columns(DataRuntime runtime, Catalog catalog, Schema schema) {
        if (null == runtime) {
            runtime = runtime();
        }
        return runtime.getAdapter().columns(runtime, catalog, schema);

    }

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

    public boolean alter(Table table) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().alter(runtime, table);
    }

    public boolean drop(Index meta) throws Exception {
        DataRuntime runtime = runtime();
        return runtime.getAdapter().drop(runtime, meta);
    }
}
