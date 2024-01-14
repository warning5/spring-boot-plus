package com.hwtx.form.domain.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatchFormValue extends FormValueDto {
    List<Long> ValueIds = Lists.newArrayList();

    public boolean isBatch() {
        return true;
    }
}
