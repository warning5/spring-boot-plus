package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwtx.form.persistence.FormChangeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单变更记录 Mapper 接口
 *
 * @author hwtx
 * @since 2024-01-17
 */
@Mapper
public interface FormChangeLogMapper extends BaseMapper<FormChangeLog> {

}
