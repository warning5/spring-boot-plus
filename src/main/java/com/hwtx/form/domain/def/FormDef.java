package com.hwtx.form.domain.def;

import com.alibaba.fastjson2.annotation.JSONField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.form.domain.FormValidate;
import com.hwtx.form.util.Util;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Data
@Slf4j
public class FormDef {

    private String formId;
    private String type;
    private List<String> actions;
    private boolean wrapWithPanel;
    private String name;
    private String api;
    private List<Item> body;
    private Map<String, Class<?>> formItemType = Maps.newHashMap();

    Map<String, List<ValidateAction>> fieldValidationAction = Maps.newHashMap();
    Map<String, List<FormValidate>> customerFormValidations = Maps.newHashMap();
    static final String validatePrefix = "validate.";
    static final String validateActionSuffix = "Predicate";
    static final String predicateClassPrefix = FormValidation.class.getName() + "$";

    @Getter
    @Builder
    public static class CustomerValidation {
        private Set<String> form;
        private Set<String> key;
        FormValidate formValidate;
        boolean forAll;
    }

    @Data
    public static class Item {
        private String type;
        private String name;
        private String label;
        private Boolean required;
        @JSONField(name = "validations")
        private ValidationsDef validationsDef;
        private Integer precision;
    }

    @Getter
    @Builder
    static class ValidateAction {
        Predicate<String> predicate;
        String msg;
    }

    public Map<String, Item> getValidateItems() {
        return body.stream().filter(item -> StringUtils.isNotEmpty(item.getName())).filter(item -> !"id".equalsIgnoreCase(item.getName()))
                .collect(Collectors.toMap(FormDef.Item::getName, Function.identity()));
    }

    @Data
    public static class ValidationsDef {
        @JSONField(name = "isAlpha")
        private Boolean isAlpha;
        private Integer minLength;
        private Integer maxLength;
        @JSONField(name = "isNumeric")
        private Boolean isNumeric;
        private Integer maximum;
        private Integer minimum;

        public void setIsAlpha(Boolean isAlpha) {
            this.isAlpha = isAlpha;
        }

        public void setIsNumeric(Boolean isNumeric) {
            this.isNumeric = isNumeric;
        }
    }

    @Builder
    @Getter
    public static class ValidationResult {
        private String message;
        private boolean pass;
        private String key;
    }

    public static void main(String[] args) throws Exception {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(FormValidation.IsAlphaPredicate.class.getName());
        Constructor<?> constructor = clazz.getDeclaredConstructor(Boolean.class);
        Object predicateObject = constructor.newInstance(true);
        System.out.println(predicateObject);
    }

    public void init(List<FormDef.CustomerValidation> customerValidations, Map<String, Class<?>> formItemType) {
        if (body != null) {
            Properties properties = new Properties();
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream("formValidate.properties"));
                for (Item itemItem : body) {
                    if (itemItem.getValidationsDef() != null) {
                        List<ValidateAction> actions = Lists.newArrayList();
                        for (Field declaredField : ValidationsDef.class.getDeclaredFields()) {
                            String fieldName = declaredField.getName();
                            String msg = (String) properties.get(validatePrefix + fieldName);
                            if (StringUtils.isEmpty(msg)) {
                                throw new RuntimeException("未能获取验证规则【" + fieldName + "】对应的验证提示消息");
                            }
                            String predicateClass = predicateClassPrefix + Util.firstCharToUpperCase(fieldName) + validateActionSuffix;
                            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(predicateClass);
                            Constructor<?> constructor = clazz.getDeclaredConstructor(declaredField.getType());
                            declaredField.setAccessible(true);
                            Object defValue = declaredField.get(itemItem.getValidationsDef());
                            if (defValue != null) {
                                Object predicateObject = constructor.newInstance(defValue);
                                actions.add(ValidateAction.builder().msg(msg).predicate((Predicate<String>) predicateObject).build());
                            }
                        }
                        fieldValidationAction.put(itemItem.getName(), actions);
                    }
                    if (itemItem.getRequired() != null) {
                        List<ValidateAction> actions = fieldValidationAction.get(itemItem.getName());
                        if (actions == null) {
                            actions = Lists.newArrayList();
                        }
                        String msg = (String) properties.get(validatePrefix + "isRequired");
                        actions.add(0, ValidateAction.builder().msg(msg).predicate(new FormValidation.RequiredPredicate()).build());
                    }
                }
            } catch (Exception e) {
                log.error("加载验证提示消息文件失败", e);
                throw new RuntimeException(e);
            }
        }
        if (!customerValidations.isEmpty()) {
            customerValidations.forEach(cv -> {
                if (cv.getForm().contains(formId)) {
                    cv.getKey().forEach(key -> {
                        FormValidate formValidate = cv.getFormValidate();
                        if (formValidate != null) {
                            customerFormValidations.compute(key, (k, v) -> {
                                if (v == null) {
                                    v = Lists.newArrayList();
                                }
                                v.add(formValidate);
                                return v;
                            });
                        }
                    });
                }
            });
        }
        this.formItemType = formItemType;
    }

    public ValidationResult validateForm(String name, String value) {
        ValidationResult validateDefault = validateDefault(name, value);
        if (!validateDefault.isPass()) {
            return validateDefault;
        }
        List<FormValidate> formValidates = customerFormValidations.get(name);
        if (formValidates != null) {
            for (FormValidate formValidate : formValidates) {
                FormDef.ValidationResult validationResult = formValidate.validate(name, value);
                if (!validationResult.isPass()) {
                    return validationResult;
                }
            }
        }
        return ValidationResult.builder().pass(true).message("验证通过").key(name).build();
    }

    private ValidationResult validateDefault(String name, String value) {
        List<ValidateAction> actions = fieldValidationAction.get(name);
        if (actions == null) {
            return ValidationResult.builder().pass(true).key(name).build();
        }
        for (ValidateAction validateAction : actions) {
            boolean ret = validateAction.getPredicate().test(value);
            if (!ret) {
                return ValidationResult.builder().pass(false).message(validateAction.getMsg()).key(name).build();
            }
        }
        return ValidationResult.builder().pass(true).message("验证通过").key(name).build();
    }
}