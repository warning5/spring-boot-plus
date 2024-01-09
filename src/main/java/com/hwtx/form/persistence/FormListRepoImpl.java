package com.hwtx.form.persistence;

import com.hwtx.form.domain.MetadataRepo;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.repo.FormListRepo;
import com.hwtx.form.domain.vo.FormListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 表单定义 服务实现类
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Slf4j
@Repository
public class FormListRepoImpl implements FormListRepo {

    @Resource
    private MetadataRepo metadataRepo;
    @Resource
    private JdbcTemplate jdbcTemplate;

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
        Long count = jdbcTemplate.queryForObject(countDsl, Long.class);
        FormListVo formListVo = new FormListVo();
        if (count == null) {
            count = 0L;
        }
        formListVo.setCount(count);
        formListVo.setRows(rows);
        return formListVo;
    }
}
