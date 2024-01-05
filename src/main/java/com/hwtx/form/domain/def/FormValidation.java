package com.hwtx.form.domain.def;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class FormValidation {

    static class RequiredPredicate implements Predicate<String> {

        boolean required = false;

        @Override
        public boolean test(String s) {
            return !required || StringUtils.isNotEmpty(s);
        }
    }

    @AllArgsConstructor
    static class IsAlphaPredicate implements Predicate<String> {
        Boolean isAlpha;

        @Override
        public boolean test(String s) {
            return !isAlpha || StringUtils.isAlpha(s);
        }
    }

    @AllArgsConstructor
    static class MaxLengthPredicate implements Predicate<String> {
        Integer maxLength;

        @Override
        public boolean test(String s) {
            return !StringUtils.isNotEmpty(s) || s.length() <= maxLength;
        }
    }

    @AllArgsConstructor
    static class MinLengthPredicate implements Predicate<String> {
        Integer minLength;

        @Override
        public boolean test(String s) {
            return !StringUtils.isNotEmpty(s) || s.length() >= minLength;
        }
    }

    @AllArgsConstructor
    static class IsNumericPredicate implements Predicate<String> {
        Boolean isNumeric;

        @Override
        public boolean test(String s) {
            return !isNumeric || StringUtils.isNumeric(s);
        }
    }

    @AllArgsConstructor
    static class MaximumPredicate implements Predicate<String> {
        Integer maximum;

        @Override
        public boolean test(String s) {
            return !StringUtils.isNotEmpty(s) || Integer.parseInt(s) < maximum;
        }
    }

    @AllArgsConstructor
    static class MinimumPredicate implements Predicate<String> {
        Integer minimum;

        @Override
        public boolean test(String s) {
            return !StringUtils.isNotEmpty(s) || Integer.parseInt(s) > minimum;
        }
    }
}
