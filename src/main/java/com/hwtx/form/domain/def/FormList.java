package com.hwtx.form.domain.def;

import lombok.Data;

import java.util.List;

@Data
public class FormList {
    private String type;
    private FormListBody body;

    @Data
    static class FormListBody {
        private List<Column> columns;
        private String title;
        private String type;
        private Filter filter;
    }

    @Data
    static class Column {
        private String name;
        private String label;
        private Boolean sortable;
        private String type;
        private Searchable searchable;
    }

    @Data
    static class Searchable {
        private String name;
        private String label;
        private String type;
        private List<Option> options;
    }

    public void buildTable() {

    }
}