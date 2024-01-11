package com.hwtx.form.domain.vo;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class FormData {
    @Builder.Default
    Map<String, Object> data = Maps.newHashMap();
}
