package com.hwtx.form.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FormListQuery {
    Long formId;
    Map<String, String[]> searchData;
}
