package com.hwtx.form.domain.repo;

import com.hwtx.form.domain.FormSearchExt;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.vo.FormData;
import com.hwtx.form.domain.vo.FormListVo;
import org.springframework.data.domain.Pageable;


/**
 * 表单值 服务接口
 *
 * @author hwtx
 * @since 2023-12-16
 */
public interface FormValueRepo {

    FormListVo query(FormDef formDef, FormListQuery formListQuery, FormSearchExt formSearchExt, String user, Pageable pageable);

    /**
     * 添加表单值
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean addFormValue(FormDef formDef, FormValueDto dto) throws Exception;

    /**
     * 修改表单值
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean updateFormValue(FormDef formDef, FormValueDto dto) throws Exception;


    /**
     * 表单值详情
     *
     * @param formDef
     * @param formValueQuery
     * @return
     * @throws Exception
     */
    FormData getFormValue(FormDef formDef, FormValueQuery formValueQuery);
}
