package com.hwtx.form.persistence;

import com.hwtx.form.domain.FormSearchExt;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.vo.FormData;
import com.hwtx.form.domain.vo.FormListVo;
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

    @Override
    public FormListVo query(FormDef formDef, FormListQuery formListQuery, FormSearchExt formSearchExt, String user, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNum = pageable.getPageNumber();
        int start = (pageNum - 1) * pageSize;
        List<Object> param = Lists.newArrayList();
        String sql = metadataRepo.buildSelectDslWithPage(formDef.getValidateItems().values(), formListQuery,
                formSearchExt, formDef, param);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormValue(FormDef formDef, FormValueDto dto) throws Exception {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValues(dto.getFormData());

        String sql = metadataRepo.buildInsertDsl(formDef.getValidateItems().values(), formDef.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        template.update(sql, parameters, keyHolder);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFormValue(FormDef formDef, FormValueDto dto) throws Exception {
        List<Object> param = Lists.newArrayList();
        String sql = metadataRepo.buildUpdateFormData(formDef, formDef.getValidateItems().values(), dto, param);
        param.add(dto.getK1());
        return jdbcTemplate.update(sql, param.toArray()) > 0;
    }

    @Override
    public FormData getFormValue(FormDef formDef, FormValueQuery formValueQuery) {
        long start = System.currentTimeMillis();
        String sql = metadataRepo.buildSearchFormData(formDef);
        log.info("构建查询表单详情数据，formId = {}, valueId = {}, spend = {}ms", formDef.getFormId(),
                formValueQuery.getValueIds(), (System.currentTimeMillis() - start));
        Map<String, Object> content = jdbcTemplate.queryForMap(sql, formValueQuery.getValueIds().get(0), formValueQuery.getUser());
        return FormData.builder().data(content).build();
    }
}
