package com.hwtx.form.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.def.FormItemType;
import com.hwtx.form.domain.ds.DatasourceDao;
import com.hwtx.form.domain.ds.JDBCAdapter;
import com.hwtx.form.domain.ds.StandardColumnType;
import com.hwtx.form.domain.ds.metadata.Column;
import com.hwtx.form.domain.ds.metadata.Table;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.dto.FormValueDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cglib.beans.BeanMap;
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
    public static final String tablePrefix = "form_";

    public boolean create(String formName, Collection<FormDef.Item> itemItems) {
        String tableName = getTableName(formName);
        Table table = new Table(tableName);
        if (datasourceDao.exists(table)) {
            log.info("数据表【{}】已存在无需创建", tableName);
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
        Table table = new Table(getTableName(formName));
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在，无法新增表单列【{}】", getTableName(formName), item.getName());
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
        Table table = new Table(getTableName(formName));
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在，无法变更表单列【{}】", getTableName(formName), newItem.getName());
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

    public Map<String, Class<?>> getColumnNameAndType(String formName) {
        String tableName = getTableName(formName);
        Table table = new Table(tableName);
        if (!datasourceDao.exists(table)) {
            log.error("数据表【{}】不存在", tableName);
            return Maps.newHashMap();
        }
        try {
            Map<String, Column> columns = datasourceDao.columns(table);
            Map<String, Class<?>> ret = Maps.newHashMap();
            for (Map.Entry<String, Column> entry : columns.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().getJavaType());
            }
            return ret;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("无法获取表列信息,table = " + tableName);
        }
    }

    public String buildInsertDsl(Collection<FormDef.Item> items, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(getTableName(name)).append("(");
        for (FormDef.Item item : items) {
            sb.append(item.getName()).append(", ");
        }
        for (String item : getCreateDefaultColumns()) {
            sb.append(item).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(") VALUES (");
        for (FormDef.Item item : items) {
            sb.append(":").append(item.getName()).append(", ");
        }
        for (String item : getCreateDefaultColumns()) {
            sb.append(":").append(item).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }

    public String buildSelectDslWithPage(Collection<FormDef.Item> items, FormListQuery formListQuery, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT id,");
        for (FormDef.Item item : items) {
            builder.append(item.getName()).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(" FROM ").append(getTableName(name));
        if (formListQuery.getFormId() != null) {
            builder.append(" WHERE ").append(FormConstants.INPUT_FORM_ID).append(" = ?");
        }
        builder.append(" AND status = 1 AND create_by = ? LIMIT ? OFFSET ? ");
        return builder.toString();
    }

    public String buildCountDsl(FormListQuery formListQuery, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append("COUNT(*) ");
        builder.append(" FROM ").append(getTableName(name));
        if (formListQuery.getFormId() != null) {
            builder.append(" WHERE ").append(FormConstants.INPUT_FORM_ID).append(" = ?");
        }
        builder.append(" AND status = 1 AND create_by = ?");
        return builder.toString();
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

    private List<String> getCreateDefaultColumns() {
        return Arrays.asList(create_time, create_by, last_modify_time, last_modify_by);
    }

    private List<String> getModifyDefaultColumns() {
        return Arrays.asList(last_modify_time, last_modify_by);
    }

    private String getTableName(String name) {
        return tablePrefix + name;
    }

    public String buildSearchFormData(FormDef formDef) {
        return "SELECT *" +
                " FROM " + getTableName(formDef.getName()) +
                " WHERE " + FormConstants.INPUT_FORM_VALUE_ID + " = ?" +
                " AND status = 1 AND create_by = ?";
    }

    public String buildUpdateFormData(FormDef formDef, Collection<FormDef.Item> items, Map<String, Object> formData, List<Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(getTableName(formDef.getName())).append(" SET ");
        items.forEach(item -> {
            builder.append(item.getName()).append(" = ?").append(", ");
            params.add(formData.get(item.getName()));
        });
        getModifyDefaultColumns().forEach(col -> builder.append(col).append(" = ?").append(", "));
        builder.delete(builder.length() - 2, builder.length());
        builder.append(" WHERE ").append(FormConstants.INPUT_FORM_VALUE_ID).append(" = ?");
        builder.append(" AND status = 1 AND create_by = ?");
        return builder.toString();
    }
}
