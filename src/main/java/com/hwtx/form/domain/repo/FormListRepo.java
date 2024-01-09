package com.hwtx.form.domain.repo;

import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.vo.FormListVo;
import org.springframework.data.domain.Pageable;

public interface FormListRepo {
    FormListVo query(FormDef formDef, FormListQuery formListQuery, String user, Pageable pageable);
}
