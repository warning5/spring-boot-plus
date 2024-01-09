package com.hwtx.form.domain.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormDefDto;
import com.hwtx.form.domain.query.FormDefAppQuery;
import com.hwtx.form.domain.vo.FormDefAppVo;
import com.hwtx.form.persistence.FormDefEntity;
import io.geekidea.boot.framework.page.Paging;


/**
 * 表单定义 服务接口
 *
 * @author hwtx
 * @since 2023-12-10
 */
public interface FormRepo extends IService<FormDefEntity> {

    /**
     * 添加表单定义
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean addFormDef(FormDefDto dto) throws Exception;

    /**
     * 修改表单定义
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean updateFormDef(FormDefDto dto) throws Exception;

    /**
     * 删除表单定义
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteFormDef(Long id) throws Exception;

    /**
     * 表单定义详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    String getFormRawContent(Long id) throws Exception;

    /**
     * App表单定义详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    FormDef getFormDef(Long id) throws Exception;

    /**
     * App表单定义分页列表
     *
     * @param query
     * @return
     * @throws Exception
     */
    Paging<FormDefAppVo> getAppFormDefPage(FormDefAppQuery query) throws Exception;

}
