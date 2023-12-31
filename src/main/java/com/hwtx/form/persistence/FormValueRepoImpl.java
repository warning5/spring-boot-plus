package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwtx.form.domain.FormValueRepo;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import io.geekidea.boot.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 表单值 服务实现类
 *
 * @author hwtx
 * @since 2023-12-16
 */
@Slf4j
@Service
public class FormValueRepoImpl extends ServiceImpl<FormValueMapper, FormValueEntity> implements FormValueRepo {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormValue(FormValueDto dto) throws Exception {
        FormValueEntity formValueEntity = new FormValueEntity();
        BeanUtils.copyProperties(dto, formValueEntity);
        return save(formValueEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFormValue(FormValueDto dto) throws Exception {
        Long id = dto.getId();
        FormValueEntity formValueEntity = getValue(dto.getId(), dto.getK1());
        if (formValueEntity == null) {
            throw new BusinessException("表单值不存在");
        }
        BeanUtils.copyProperties(dto, formValueEntity);
        return updateById(formValueEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteFormValue(Long id) throws Exception {
        return removeById(id);
    }

    @Override
    public FormValueEntity getFormValue(FormValueQuery formValueQuery) {
        return getValue(formValueQuery.getValueId(), formValueQuery.getUser());
    }

    private FormValueEntity getValue(Long id, String k1) {
        LambdaQueryWrapper<FormValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormValueEntity::getId, id);
        wrapper.eq(FormValueEntity::getK1, k1);
        return getOne(wrapper);
    }
}
