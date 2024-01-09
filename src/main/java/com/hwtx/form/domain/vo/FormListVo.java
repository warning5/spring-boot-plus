package com.hwtx.form.domain.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormListVo {
    long count;
    List<Map<String, Object>> rows;
}
