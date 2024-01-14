package com.hwtx.form.util;

import com.hwtx.form.domain.def.FormDef;
import io.geekidea.boot.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class Util {

    public static String firstCharToUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static Object getFormItemValue(FormDef formDef, String itemName, String itemValue) {
        Class<?> itemType = formDef.getFormItemType().get(itemName);
        if (itemType != null) {
            if (StringUtils.isNumeric(itemValue) && Objects.equals(itemType, Integer.class)) {
                return Integer.valueOf(itemValue);
            } else if (StringUtils.isNumeric(itemValue) && Objects.equals(itemType, Long.class)) {
                return Long.valueOf(itemValue);
            }
        }
        return itemValue;
    }
}
