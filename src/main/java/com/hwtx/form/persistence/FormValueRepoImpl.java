package com.hwtx.form.persistence;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwtx.form.domain.FormValueRepo;
import com.hwtx.form.domain.MetadataRepo;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import io.geekidea.boot.common.constant.SystemConstant;
import io.geekidea.boot.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 表单值 服务实现类
 *
 * @author hwtx
 * @since 2023-12-16
 */
@Slf4j
@Service
public class FormValueRepoImpl extends ServiceImpl<FormValueMapper, FormValueEntity> implements FormValueRepo {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private MetadataRepo metadataRepo;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormValue(FormValueDto dto, FormDef formDef) throws Exception {
        FormValueEntity formValueEntity = new FormValueEntity();
        BeanUtils.copyProperties(dto, formValueEntity);
        save(formValueEntity);
        Map<String, Object> target = JSON.parseObject(dto.getContent(), Map.class);
        setDefaultValueInfo(target, dto);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValues(target);

        String sql = metadataRepo.buildInsertDsl(formDef.getValidateItems().values(), formDef.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        template.update(sql, parameters, keyHolder);
        return true;
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

    private void setDefaultValueInfo(Map<String, Object> target, FormValueDto dto) {
        target.put(SystemConstant.create_by, dto.getCreateBy());
        target.put(SystemConstant.create_time, dto.getCreateTime());
        target.put(SystemConstant.last_modify_by, dto.getLastModifyBy());
        target.put(SystemConstant.last_modify_time, dto.getLastModifyTime());
    }
}
