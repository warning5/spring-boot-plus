package com.hwtx.form.domain;

import com.hwtx.form.persistence.ds.WhereOp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public interface FormSearchExt {

    List<SearchFieldInfo> getSearchFields(String searchItem);

    List<String> searchItems();

    @AllArgsConstructor
    @Getter
    class SearchFieldInfo {
        String fieldName;
        WhereOp whereOp;
    }
}
