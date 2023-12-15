package com.hwtx.form.domain;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hwtx.form.annotation.FormValidation;
import io.geekidea.boot.framework.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class FormService {

    public static final String INPUT_FORM_ID = "formId";

    @Resource
    ApplicationContext applicationContext;
    private final List<FormDef.CustomerValidation> customerValidations = Lists.newArrayList();

    @PostConstruct
    public void setup() {
        Map<String, FormValidate> formValidates = applicationContext.getBeansOfType(FormValidate.class);
        formValidates.values().forEach(v -> {
            FormValidation formValidation = v.getClass().getAnnotation(FormValidation.class);
            if (formValidation != null) {
                if (!formValidation.enable()) {
                    return;
                }
                customerValidations.add(FormDef.CustomerValidation.builder().formValidate(v).key(Sets.newHashSet(formValidation.key()))
                        .form(Sets.newHashSet(formValidation.form())).build());
            } else {
                customerValidations.add(FormDef.CustomerValidation.builder().formValidate(v).forAll(true).build());
            }
        });
    }

    @Resource
    FormRepo formRepo;
    private static final Cache<Long, FormDef> formCache = CacheBuilder.newBuilder()
            // 供应商数量目前不超过2W, 占用内存可控, 暂不设置过期设置预期最大值
            .maximumSize(50000)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .recordStats()
            .build();

    public String getRawFormDef(Long formId) throws Exception {
        String formContent = formRepo.getFormRawContent(formId);
        if (StringUtils.isNotEmpty(formContent)) {
            return formContent;
        }
        return "";
    }

    public Map<String, String> validateForm(Long formId, Map<String, String> formValues) throws Exception {

        FormDef formDef = formCache.getIfPresent(formId);
        if (formDef == null) {
            formDef = formRepo.getFormDef(formId);
            if (formDef != null) {
                formCache.put(formId, formDef);
                formDef.init(customerValidations);
            }
        }
        if (formDef == null) {
            throw new BusinessException("表单定义不存在");
        }

        Map<String, String> validationResultMap = new HashMap<>();

        FormDef finalFormDef = formDef;
        formValues.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), INPUT_FORM_ID))
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    FormDef.ValidationResult validationResult = finalFormDef.validateForm(key, value);
                    if (!validationResult.isPass()) {
                        validationResultMap.put(validationResult.getKey(), validationResult.getMessage());
                    }
                });
        return validationResultMap;
    }
}
