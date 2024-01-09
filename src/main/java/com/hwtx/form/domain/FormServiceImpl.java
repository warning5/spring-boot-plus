package com.hwtx.form.domain;

import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hwtx.form.annotation.FormValidation;
import com.hwtx.form.controller.FormHandleParam;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormValueDto;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.repo.FormRepo;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.service.FormService;
import com.hwtx.form.persistence.FormValueEntity;
import io.geekidea.boot.config.properties.BootProperties;
import io.geekidea.boot.framework.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FormServiceImpl implements FormService {

    public static final String INPUT_FORM_ID = "formId";
    public static final String INPUT_FORM_VALUE_ID = "id";
    public static final String INPUT_FORM_PAGE = "pageX";

    @Resource
    ApplicationContext applicationContext;
    @Resource
    FormRepo formRepo;
    @Resource
    FormValueRepo formValueRepo;
    @Resource
    MetadataRepo metadataRepo;
    @Resource
    private BootProperties bootProperties;

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

    private static final Cache<Long, FormDef> formCache = CacheBuilder.newBuilder()
            .maximumSize(50000)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .recordStats()
            .build();

    @Override
    public String getRawFormDef(Long formId) throws Exception {
        String formContent = formRepo.getFormRawContent(formId);
        if (StringUtils.isNotEmpty(formContent)) {
            return formContent;
        }
        return "";
    }

    @Override
    public FormDef getFormDef(Long formId) throws Exception {
        FormDef formDef = formCache.getIfPresent(formId);
        if (formDef == null) {
            formDef = formRepo.getFormDef(formId);
            if (formDef != null) {
                formInit(formDef);
                if (bootProperties.isEnableFormDefCache()) {
                    formCache.put(formId, formDef);
                }
            }
        }
        return formDef;
    }

    @Override
    public void saveFormData(Map<String, String> formData, String user) throws Exception {
        FormValueDto formValue = new FormValueDto();
        formValue.setForm(formData.get(INPUT_FORM_ID));
        formValue.setContent(JSON.toJSONString(formData));
        formValue.setPage(formData.get(INPUT_FORM_PAGE));

        formValue.setLastModifyBy(user);
        formValue.setLastModifyTime(new Date());
        formValue.setK1(user);

        if (StringUtils.isEmpty(formData.get(INPUT_FORM_VALUE_ID))) {
            formValue.setCreateTime(new Date());
            formValue.setCreateBy(user);
            FormDef formDef = getFormDef(Long.parseLong(formValue.getForm()));
            if (formDef != null) {
                formValueRepo.addFormValue(formValue, formDef);
            }
        } else {
            formValue.setId(Long.parseLong(formData.get(INPUT_FORM_VALUE_ID)));
            formValueRepo.updateFormValue(formValue);
        }
    }

    @Override
    public Map<String, String> validateForm(Long formId, Map<String, String> formValues) throws Exception {

        FormDef formDef = getFormDef(formId);
        if (formDef == null) {
            throw new BusinessException("表单定义不存在");
        }

        Map<String, String> validationResultMap = new HashMap<>();

        formValues.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), INPUT_FORM_ID))
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    FormDef.ValidationResult validationResult = formDef.validateForm(key, value);
                    if (!validationResult.isPass()) {
                        validationResultMap.put(validationResult.getKey(), validationResult.getMessage());
                    }
                });
        return validationResultMap;
    }

    @Override
    public String getFormData(FormValueQuery formValueQuery) throws Exception {
        FormValueEntity formValueVo = formValueRepo.getFormValue(formValueQuery);
        if (formValueVo != null) {
            return formValueVo.getContent();
        }

        return null;
    }

    @Override
    public void removeValue(FormValueQuery formValueQuery) throws Exception {
        FormValueDto formValue = new FormValueDto();
        formValue.setLastModifyBy(formValueQuery.getUser());
        formValue.setLastModifyTime(new Date());
        formValue.setId(formValueQuery.getValueId());
        formValue.setK1(formValueQuery.getUser());
        formValue.setStatus(false);
        formValueRepo.updateFormValue(formValue);
    }

    @Override
    public void handleForm(FormHandleParam formHandleParam) throws Exception {
        FormDef formDef = formRepo.getFormDef(formHandleParam.getFormId());
        if (formDef == null) {
            return;
        }
        String formName = formDef.getName();
        Map<String, FormDef.Item> items = formDef.getValidateItems();
        switch (FormHandleAction.valueOf(formHandleParam.getHandleAction())) {
            case CREATE:
                metadataRepo.create(formName, items.values());
                break;
            case ADD:
                FormDef.Item addItem = items.get(formHandleParam.getAddItem());
                if (addItem != null) {
                    metadataRepo.add(formName, addItem);
                }
                break;
            case UPDATE:
                FormDef.Item updateItem = items.get(formHandleParam.getUpdateToItem());
                if (updateItem != null) {
                    metadataRepo.update(formName, formHandleParam.getUpdateFromItem(), updateItem);
                }
            case INDEX:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException("Unexpected value: " + FormHandleAction.valueOf(formHandleParam.getHandleAction()));
        }
    }

    private void formInit(FormDef formDef) {
        boolean ret = metadataRepo.create(formDef.getName(), formDef.getValidateItems().values());
        if (ret) {
            Map<String, Class<?>> formItemType = metadataRepo.getColumnNameAndType(formDef.getName());
            formDef.init(customerValidations, formItemType);
        }
    }
}