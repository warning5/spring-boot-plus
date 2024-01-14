package com.hwtx.form.domain;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hwtx.form.annotation.FormId;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.service.FormService;
import com.hwtx.form.domain.vo.FormListVo;
import io.geekidea.boot.framework.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FormListService {

    @Resource
    private FormService formService;
    @Resource
    private FormValueRepo formListRepo;

    private static final Cache<String, FormSearchExt> searchExtCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .recordStats()
            .build();

    @PostConstruct
    public void setup() {
        Map<String, FormSearchExt> searchExts = SpringUtil.getApplicationContext().getBeansOfType(FormSearchExt.class);
        searchExts.values().forEach(searchExt -> {
            FormId formId = searchExt.getClass().getAnnotation(FormId.class);
            if (formId != null) {
                FormSearchExt formSearchExt = searchExtCache.getIfPresent(formId.value());
                if (formSearchExt != null) {
                    throw new BusinessException(MessageFormat.format("表单查询扩展已经存在，formId = {0}", formId.value()));
                } else {
                    searchExtCache.put(formId.value(), searchExt);
                }
            }
        });
    }

    public FormListVo list(FormListQuery formListQuery, String user, Pageable pageable) throws Exception {
        FormDef formDef = formService.getFormDef(formListQuery.getFormId());
        if (formDef == null) {
            throw new RuntimeException("表单标识不正确");
        }
        FormSearchExt searchExt = searchExtCache.getIfPresent(formDef.getFormId());
        return formListRepo.query(formDef, formListQuery, searchExt, user, pageable);
    }
}
