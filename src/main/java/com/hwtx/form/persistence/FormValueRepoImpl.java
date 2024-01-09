package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.MetadataRepo;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import io.geekidea.boot.common.constant.SystemConstant;
import io.geekidea.boot.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
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
    ObjectMapper mapper = new ObjectMapper();
    ThreadLocal<FormDef> formDefThreadLocal = new ThreadLocal<>();

    {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Object.class, new FormTypeDeserializer());
        mapper.registerModule(module);
    }

    public class FormTypeDeserializer extends JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            FormDef formDef = formDefThreadLocal.get();
            if (formDef == null) {
                throw new BusinessException("无法获取表单定义");
            }
            Class<?> itemType = formDef.getFormItemType().get(p.getCurrentName());
            if (itemType != null) {
                if (Objects.equals(itemType, Integer.class)) {
                    return Integer.valueOf(p.getValueAsString());
                } else if (Objects.equals(itemType, Long.class)) {
                    return Long.valueOf(p.getValueAsString());
                }
            }
            return p.getValueAsString();
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String content = "{\"engine\":\"eeeer\",\"browser\":\"13u8dd\",\"platform\":\"iOS\",\"grade\":\"2\",\"formId\":\"2\",\"pageX\":\"page1\",\"id\":\"3\"}";
        FormValueRepoImpl formValueRepo = new FormValueRepoImpl();
        Map model = formValueRepo.mapper.readValue(content, Map.class);
        System.out.println(model);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormValue(FormValueDto dto, FormDef formDef) throws Exception {
        FormValueEntity formValueEntity = new FormValueEntity();
        BeanUtils.copyProperties(dto, formValueEntity);
        save(formValueEntity);
        try {
            formDefThreadLocal.set(formDef);
            Map<String, Object> target = mapper.readValue(dto.getContent(), Map.class);
            setDefaultValueInfo(target, dto);
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValues(target);

            String sql = metadataRepo.buildInsertDsl(formDef.getValidateItems().values(), formDef.getName());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
            template.update(sql, parameters, keyHolder);
        } finally {
            formDefThreadLocal.remove();
        }
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
