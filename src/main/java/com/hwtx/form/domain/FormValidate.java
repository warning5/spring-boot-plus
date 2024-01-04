package com.hwtx.form.domain;

import com.hwtx.form.domain.def.FormDef;

public interface FormValidate {
    FormDef.ValidationResult validate(String name, String value);
}
