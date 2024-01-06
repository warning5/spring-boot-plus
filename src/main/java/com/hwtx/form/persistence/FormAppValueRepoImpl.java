package com.hwtx.form.persistence;

import com.google.common.collect.Maps;
import com.hwtx.form.util.Util;
import com.hwtx.form.domain.FormAppValueRepo;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtField;
import org.apache.ibatis.javassist.CtNewMethod;
import org.apache.ibatis.javassist.bytecode.AccessFlag;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class FormAppValueRepoImpl implements FormAppValueRepo {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        new FormAppValueRepoImpl().query();
    }
    @Override
    public void query() {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("com.hwtx.form.domain.persistence.Form_add");
        Map<String, String> fields = Maps.newHashMap();
        fields.put("form", String.class.getName());
        fields.put("page", String.class.getName());
        fields.put("content", String.class.getName());
        fields.put("k1", String.class.getName());
        fields.put("k2", String.class.getName());
        fields.put("status", int.class.getName());
        try {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                CtField param = new CtField(pool.get(entry.getValue()), entry.getKey(), cc);
                param.setModifiers(AccessFlag.PRIVATE);
                String name = Util.firstCharToUpperCase(entry.getKey());
                cc.addMethod(CtNewMethod.setter("set" + name, param));
                cc.addMethod(CtNewMethod.getter("get" + name, param));
                cc.addField(param);
            }

            Class<?> clazz = cc.toClass();
            String sql = "SELECT * FROM form_value where id = 3";
            Object result = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(clazz));
            System.out.println(result);
        } catch (Exception e) {

        }
    }
}
