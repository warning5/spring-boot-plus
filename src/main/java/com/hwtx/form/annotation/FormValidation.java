package com.hwtx.form.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FormValidation {
    String[] form() default {};

    String[] key() default {};

    boolean enable() default true;
}
