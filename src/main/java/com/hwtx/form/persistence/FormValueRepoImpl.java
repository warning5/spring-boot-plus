package com.hwtx.form.persistence;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hwtx.form.domain.MetadataRepo;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.ds.DefaultColumn;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.vo.FormData;
import com.hwtx.form.domain.vo.FormListVo;
import io.geekidea.boot.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
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
public class FormValueRepoImpl implements FormValueRepo {

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

    @Override
    public FormListVo query(FormDef formDef, FormListQuery formListQuery, String user, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNum = pageable.getPageNumber();
        int start = (pageNum - 1) * pageSize;
        String sql = metadataRepo.buildSelectDslWithPage(formDef.getValidateItems().values(), formListQuery, formDef.getName());
        List<Object> param = Lists.newArrayList();
        param.add(formDef.getFormId());
        param.add(user);
        param.add(pageSize);
        param.add(start);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, param.toArray());
        String countDsl = metadataRepo.buildCountDsl(formListQuery, formDef.getName());
        Object[] params = new Object[]{formDef.getFormId(), user};
        Long count = jdbcTemplate.queryForObject(countDsl, Long.class, params);
        FormListVo formListVo = new FormListVo();
        if (count == null) {
            count = 0L;
        }
        formListVo.setCount(count);
        formListVo.setRows(rows);
        return formListVo;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormValue(FormDef formDef, FormValueDto dto) throws Exception {
        try {
            formDefThreadLocal.set(formDef);
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValues(dto.getFormData());

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
    public boolean updateFormValue(FormDef formDef, FormValueDto dto) throws Exception {
        List<Object> param = Lists.newArrayList();
        String sql = metadataRepo.buildUpdateFormData(formDef, formDef.getValidateItems().values(), dto.getFormData(), param);
        param.add(dto.getId());
        param.add(dto.getK1());
        return jdbcTemplate.update(sql, param.toArray()) > 0;
    }

    @Override
    public FormData getFormValue(FormDef formDef, FormValueQuery formValueQuery) {
        String sql = metadataRepo.buildSearchFormData(formDef);
        Map<String, Object> content = jdbcTemplate.queryForMap(sql, formValueQuery.getValueId(), formValueQuery.getUser());
        return FormData.builder().data(content).build();
    }
}
