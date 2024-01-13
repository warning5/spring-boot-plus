package com.hwtx.form.persistence.ds;

import com.hwtx.form.persistence.ds.metadata.Column;
import com.hwtx.form.persistence.ds.metadata.Index;
import com.hwtx.form.persistence.ds.metadata.PrimaryKey;
import com.hwtx.form.persistence.ds.metadata.Table;
import com.hwtx.form.persistence.ds.mysql.MySQLGenusAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DatasourceDao {
    private final DataRuntime runtime;

    DatasourceDao(JdbcTemplate jdbcTemplate) {
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

    public Map<String, Column> columns(Table table) throws Exception {
        return runtime.getAdapter().columns(runtime, table, null);

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
