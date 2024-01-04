package com.hwtx.form.domain.service;

import com.hwtx.form.annotation.FormValidation;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.FormValidate;

import java.util.Objects;

@FormValidation(form = {"1", "2", "3", "4"}, key = {"grade", "platform"})
public class AddFormValidate implements FormValidate {
    @Override
    public FormDef.ValidationResult validate(String name, String value) {
        if (Objects.equals(name, "platform")) {
            if (Objects.equals(value, "iOS") || Objects.equals(value, "Android")) {
                return FormDef.ValidationResult.builder().pass(true).build();
            } else {
                return FormDef.ValidationResult.builder().pass(false).message("platform只能设定为iOS或Android").key(name).build();
            }
        } else {
            try {
                int grade = Integer.parseInt(value);
                if (grade < 1 || grade > 12) {
                    return FormDef.ValidationResult.builder().pass(false).message("grade只能设定为1到3之间的整数").key(name).build();
                } else {
                    return FormDef.ValidationResult.builder().pass(true).build();
                }
            } catch (NumberFormatException e) {
                return FormDef.ValidationResult.builder().pass(false).message("grade只能设定为整数").key(name).build();
            }
        }
    }
}
