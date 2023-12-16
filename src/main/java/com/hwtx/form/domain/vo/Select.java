package com.hwtx.form.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class Select {
    private List<Option> options;

    @Data
    static
    public class Option {
        private String label;
        private String value;
    }
}

