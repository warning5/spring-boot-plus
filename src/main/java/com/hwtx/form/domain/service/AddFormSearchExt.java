package com.hwtx.form.domain.service;

import com.hwtx.form.annotation.FormId;
import com.hwtx.form.domain.FormSearchExt;
import com.hwtx.form.persistence.ds.WhereOp;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@FormId("2")
public class AddFormSearchExt implements FormSearchExt {
    @Override
    public List<SearchFieldInfo> getSearchFields(String searchItem) {
        return Arrays.asList(new SearchFieldInfo("platform", WhereOp.EQ), new SearchFieldInfo("grade", WhereOp.EQ));
    }

    @Override
    public List<String> searchItems() {
        return Collections.singletonList("keywords");
    }
}
