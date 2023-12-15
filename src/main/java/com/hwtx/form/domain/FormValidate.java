package com.hwtx.form.domain;

import java.util.Map;

public interface FormValidate {
    FormDef.ValidationResult validate(String name, String value);
}
