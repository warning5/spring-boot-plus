package com.hwtx.form.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtField;
import org.apache.ibatis.javassist.CtNewMethod;
import org.apache.ibatis.javassist.bytecode.AccessFlag;

import java.util.Map;

@Slf4j
public class Util {

    public static String firstCharToUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static Class<?> buildClass(String className, Map<String, String> items) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(className);
        try {
            for (Map.Entry<String, String> entry : items.entrySet()) {
                CtField param = new CtField(pool.get(entry.getValue()), entry.getKey(), cc);
                param.setModifiers(AccessFlag.PRIVATE);
                String name = Util.firstCharToUpperCase(entry.getKey());
                cc.addMethod(CtNewMethod.setter("set" + name, param));
                cc.addMethod(CtNewMethod.getter("get" + name, param));
                cc.addField(param);
            }

            return cc.toClass();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
