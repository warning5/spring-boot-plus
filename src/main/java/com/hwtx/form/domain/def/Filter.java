package com.hwtx.form.domain.def;

import lombok.Data;

import java.util.List;

@Data
public class Filter {
    private List<FilterBody> body;

    @Data
    static class FilterBody {
        private String name;
        private String type;
    }
}