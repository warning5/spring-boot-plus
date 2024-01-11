package com.hwtx.form.domain;

import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.repo.FormValueRepo;
import com.hwtx.form.domain.service.FormService;
import com.hwtx.form.domain.vo.FormListVo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FormListService {

    @Resource
    private FormService formService;
    @Resource
    private FormValueRepo formListRepo;

    public FormListVo list(FormListQuery formListQuery, String user, Pageable pageable) throws Exception {
        FormDef formDef = formService.getFormDef(formListQuery.getFormId());
        if (formDef == null) {
            throw new RuntimeException("表单标识不正确");
        }
        return formListRepo.query(formDef, formListQuery, user, pageable);

    }
//    public void add() {
//        String tableName = "test";
//        Table table = new Table(tableName);
//        boolean exist = dao.exists(table);
//        if (!exist) {
//            table.addColumn("ID", "bigint", 12, 11).primary(true).autoIncrement(true).setComment("主键");
//            table.addColumn("CODE", "varchar(20)").setComment("编号");
//            table.addColumn("DEFAULT_NAME", "varchar(50)").setComment("名称").setDefaultValue("A");
//            table.addColumn("NAME", "varchar(50)").setComment("名称");
//            table.addColumn("O_NAME", "varchar(50)").setComment("原列表");
//            table.addColumn("SALARY", "decimal(10,2)").setComment("精度").setNullable(false);
//            table.addColumn("SALARY_12", "decimal(10,2)").setComment("精度").setNullable(false);
//            table.addColumn("DEL_COL", "varchar(50)").setComment("删除");
//            table.addColumn("CREATE_TIME", "datetime").setComment("创建时间").setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
//            try {
//                dao.create(table);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        Table update = new Table(tableName);
//
//        Column column = new Column();
//        column.setName("ext");
//        column.nullable(false);
//        column.setDefaultValue("123");
//        column.setTypeName("varchar(20)");
//
//        try {
//            dao.alter(update, column);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        Column modify = new Column();
//        modify.setName("ext");
//        modify.setDefaultValue("AS");
//        modify.setTypeName("varchar(100)");
//        modify.setComment("测试");
//
//        column.setUpdate(modify, false, false);
//        try {
//            dao.alter(update, column);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
////
//        Table autoCreate = new Table("auto_create");
////        autoCreate.setComment("自动创建");
////        update.setUpdate(autoCreate, false, false);
////        try {
////            dao.alter(update);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
//
//        Index index = new Index();
//        index.setComment("测试索引");
//        index.setName("test");
//        LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
//        columns.put("NAME", new Column("NAME"));
//        columns.put("CODE", new Column("CODE"));
//        index.setColumns(columns);
//
//        index.setTable(autoCreate);
//
//        try {
//            dao.add(index);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
