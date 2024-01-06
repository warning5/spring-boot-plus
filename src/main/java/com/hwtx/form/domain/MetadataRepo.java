package com.hwtx.form.domain;

import com.google.common.collect.Maps;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.def.FormItemType;
import com.hwtx.form.domain.ds.DatasourceDao;
import com.hwtx.form.domain.ds.JDBCAdapter;
import com.hwtx.form.domain.ds.StandardColumnType;
import com.hwtx.form.domain.ds.metadata.Column;
import com.hwtx.form.domain.ds.metadata.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static io.geekidea.boot.common.constant.SystemConstant.*;

@Repository
@Slf4j
public class MetadataRepo {

    @Resource
    private DatasourceDao datasourceDao;

    public boolean create(String formName, Collection<FormDef.Item> itemItems) {
        Table table = new Table(formName);
        if (datasourceDao.exists(table)) {
            log.info("数据表【{}】已存在无需创建", formName);
            return true;
        }
        table.addColumn("id", StandardColumnType.BIGINT.getName()).primary(true).autoIncrement(true).setComment("主键").setNullable(false);
        buildItemColumn(itemItems).forEach(table::addColumn);
        table.addColumn("status", StandardColumnType.TINYINT.getName()).setPrecision(1).setComment("逻辑删除 1:正常  0:删除").setNullable(true).setDefaultValue(1);
        table.addColumn("create_time", StandardColumnType.DATETIME.getName()).setComment("创建时间").setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
        table.addColumn("create_by", StandardColumnType.VARCHAR.getName()).setPrecision(64).setComment("创建者").setDefaultValue("");
        table.addColumn("last_modify_time", StandardColumnType.DATETIME.getName()).setComment("最后修改时间").setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
        table.addColumn("last_modify_by", StandardColumnType.VARCHAR.getName()).setPrecision(64).setComment("修改人").setDefaultValue("");
        try {
            return datasourceDao.create(table);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("创建表单数据表失败");
        }
    }

    public boolean add(String formName, FormDef.Item item) {
        Table table = new Table(formName);
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在，无法新增表单列【{}】", formName, item.getName());
            return false;
        }
        Column column = buildColumn(item);
        try {
            return datasourceDao.alter(table, column);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("新增表单字段列失败");
        }
    }

    public boolean update(String formName, String oldColumnName, FormDef.Item newItem) {
        Table table = new Table(formName);
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在，无法变更表单列【{}】", formName, newItem.getName());
            return false;
        }
        Column newColumn = buildColumn(newItem);
        Column column = new Column(oldColumnName);
        column.setUpdate(newColumn);
        try {
            return datasourceDao.alter(table, column);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("新增表单字段列失败");
        }
    }

    public Map<String, String> getColumnNameAndType(String formName) {
        Table table = new Table(formName);
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在", formName);
            return Maps.newHashMap();
        }
        try {
            Map<String, Column> columns = datasourceDao.columns(table);
            Map<String, String> ret = Maps.newHashMap();
            for (Map.Entry<String, Column> entry : columns.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().getJavaType().getName());
            }
            return ret;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("无法获取表列信息,table = " + formName);
        }
    }

    public String buildInsertDsl(Collection<FormDef.Item> items, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(name).append("(");
        for (FormDef.Item item : items) {
            sb.append(item.getName()).append(", ");
        }
        for (String item : getDdDefaultColumns()) {
            sb.append(item).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(") VALUES (");
        for (FormDef.Item item : items) {
            sb.append(":").append(item.getName()).append(", ");
        }
        for (String item : getDdDefaultColumns()) {
            sb.append(":").append(item).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }

    private List<Column> buildItemColumn(Collection<FormDef.Item> itemItems) {
        return itemItems.stream().map(this::buildColumn).collect(Collectors.toList());
    }

    private Column buildColumn(FormDef.Item item) {
        Column column = new Column();
        FormDef.ValidationsDef validationsDef = item.getValidationsDef();
        if (validationsDef != null) {
            if (BooleanUtils.isTrue(validationsDef.getIsNumeric()) || Objects.equals(FormItemType.INPUT_NUMBER.getType(), item.getType())) {
                if (item.getPrecision() != null) {
                    column.setName(item.getName()).setTypeName(StandardColumnType.DECIMAL.getName()).setPrecision(10, item.getPrecision()).setComment(item.getLabel());
                } else {
                    column.setName(item.getName()).setTypeName(StandardColumnType.INT.getName()).setComment(item.getLabel());
                }
            } else if (BooleanUtils.isTrue(validationsDef.getIsAlpha())) {
                int length = 200;
                if (validationsDef.getMaxLength() != null) {
                    length = validationsDef.getMaxLength();
                }
                if (validationsDef.getMinLength() != null) {
                    length += validationsDef.getMinLength();
                }
                column.setName(item.getName()).setTypeName(StandardColumnType.VARCHAR.getName()).setPrecision(length).setComment(item.getLabel());
            }
        } else {
            column.setName(item.getName()).setTypeName(StandardColumnType.VARCHAR.getName()).setPrecision(200).setComment(item.getLabel());
        }
        if (BooleanUtils.isTrue(item.getRequired())) {
            column.setNullable(false);
        }
        return column;
    }

    private List<String> getDdDefaultColumns() {
        return Arrays.asList(create_time, create_by, last_modify_time, last_modify_by);
    }
}
