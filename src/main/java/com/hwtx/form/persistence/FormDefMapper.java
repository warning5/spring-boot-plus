package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.query.FormDefAppQuery;
import com.hwtx.form.domain.vo.FormDefAppVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表单定义 Mapper 接口
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Mapper
public interface FormDefMapper extends BaseMapper<FormDefEntity> {

    /**
     * 表单定义详情
     *
     * @param id
     * @return
     */
    FormDefEntity getFormDefById(Long id);

    /**
     * 表单定义分页列表
     *
     * @param query
     * @return
     */
    List<FormDefEntity> getFormDefPage(FormValueQuery query);

    /**
     * App表单定义详情
     *
     * @param id
     * @return
     */
    FormDefEntity getAppFormDefById(Long id);

    /**
     * App表单定义分页列表
     *
     * @param query
     * @return
     */
    List<FormDefAppVo> getAppFormDefPage(FormDefAppQuery query);

}
