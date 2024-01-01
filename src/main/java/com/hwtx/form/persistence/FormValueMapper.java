package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwtx.framework.domain.vo.FormValueVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单值 Mapper 接口
 *
 * @author hwtx
 * @since 2023-12-16
 */
@Mapper
public interface FormValueMapper extends BaseMapper<FormValueEntity> {

    /**
     * 表单值详情
     *
     * @param id
     * @return
     */
    FormValueVo getFormValueById(Long id);
}
