package com.hwtx.form.domain;

public interface FormValidate {
    FormDef.ValidationResult validate(String name, String value);
}
