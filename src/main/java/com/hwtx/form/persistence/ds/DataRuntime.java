package com.hwtx.form.persistence.ds;

import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DataRuntime {
    @Getter
    protected JdbcTemplate processor;
    protected DriverAdapter adapter;

    DataRuntime(JdbcTemplate jdbcTemplate, DriverAdapter adapter) {
        this.processor = jdbcTemplate;
        this.adapter = adapter;
    }

    DriverAdapter getAdapter() {
        return adapter;
    }

    public DataSource datasource() {
        return processor.getDataSource();
    }
}