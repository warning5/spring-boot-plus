//package com.hwtx.form.persistence;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
//import com.google.common.collect.Maps;
//import com.hwtx.form.util.Util;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.javassist.ClassPool;
//import org.apache.ibatis.javassist.CtClass;
//import org.apache.ibatis.javassist.CtField;
//import org.apache.ibatis.javassist.CtNewMethod;
//import org.apache.ibatis.javassist.bytecode.AccessFlag;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
///**
// * 表单定义 服务实现类
// *
// * @author hwtx
// * @since 2023-12-10
// */
//@Slf4j
//@Repository
//public class FormListRepoImpl extends ServiceImpl<FormListMapper, List<Map<String, Object>>> implements FormListRepo {
//
//    @Resource
//    private FormListMapper formListMapper;
//
//    @Override
//    public List<Map<String, Object>> query(String sql) {
//
//        ClassPool pool = ClassPool.getDefault();
//        CtClass cc = pool.makeClass("com.example.DynamicPojo");
//        Map<String, String> fields = Maps.newHashMap();
//        fields.put("form", String.class.getName());
//        fields.put("page", String.class.getName());
//        fields.put("content", String.class.getName());
//        fields.put("k1", String.class.getName());
//        fields.put("k2", String.class.getName());
//        fields.put("status", int.class.getName());
//        try {
//            for (Map.Entry<String, String> entry : fields.entrySet()) {
//                CtField param = new CtField(pool.get(entry.getValue()), entry.getKey(), cc);
//                param.setModifiers(AccessFlag.PRIVATE);
//                String name = Util.firstCharToUpperCase(entry.getKey());
//                cc.addMethod(CtNewMethod.setter("set" + name, param));
//                cc.addMethod(CtNewMethod.getter("get" + name, param));
//                cc.addField(param);
//            }
//
//            Class<?> clazz = cc.toClass();
//            String sql1 = "SELECT * FROM form_value where id = 3";
//            List<Map<String, Object>> result = new SqlRunner(FormListMapper.class).selectList(sql1, Map.class);
//            System.out.println(result);
//        } catch (Exception e) {
//            log.error("", e);
//        }
//
//        return null;
//    }
//
//}
