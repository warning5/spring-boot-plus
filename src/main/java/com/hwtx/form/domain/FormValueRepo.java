package com.hwtx.form.domain;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.persistence.FormValueEntity;
import com.hwtx.framework.domain.vo.FormValueVo;


/**
 * 表单值 服务接口
 *
 * @author hwtx
 * @since 2023-12-16
 */
public interface FormValueRepo extends IService<FormValueEntity> {

    /**
     * 添加表单值
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean addFormValue(FormValueDto dto) throws Exception;

    /**
     * 修改表单值
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean updateFormValue(FormValueDto dto) throws Exception;

    /**
     * 删除表单值
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteFormValue(Long id) throws Exception;

    /**
     * 表单值详情
     *
     * @param formValueQuery
     * @return
     * @throws Exception
     */
    public FormValueEntity getFormValue(FormValueQuery formValueQuery);
}
