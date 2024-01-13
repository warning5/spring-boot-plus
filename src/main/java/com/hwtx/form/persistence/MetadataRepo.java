package com.hwtx.form.persistence;

import com.google.common.collect.Maps;
import com.hwtx.form.domain.FormConstants;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.def.FormItemType;
import com.hwtx.form.persistence.ds.DatasourceDao;
import com.hwtx.form.persistence.ds.DefaultColumn;
import com.hwtx.form.persistence.ds.JDBCAdapter;
import com.hwtx.form.persistence.ds.StandardColumnType;
import com.hwtx.form.persistence.ds.metadata.Column;
import com.hwtx.form.persistence.ds.metadata.Table;
import com.hwtx.form.domain.dto.FormListQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.hwtx.form.persistence.ds.DefaultColumn.*;

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
        table.addColumn(DefaultColumn.id.name(), StandardColumnType.BIGINT.getName()).primary(true).autoIncrement(true)
                .setComment(DefaultColumn.id.getComment()).setNullable(false);
        buildItemColumn(itemItems).forEach(table::addColumn);
        table.addColumn(DefaultColumn.status.name(), StandardColumnType.TINYINT.getName()).setPrecision(1)
                .setComment(DefaultColumn.status.getComment()).setNullable(true).setDefaultValue(1);
        table.addColumn(create_time.name(), StandardColumnType.DATETIME.getName())
                .setComment(create_time.getComment()).setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
        table.addColumn(create_by.name(), StandardColumnType.VARCHAR.getName()).setPrecision(64)
                .setComment(create_by.getComment()).setDefaultValue("");
        table.addColumn(last_modify_time.name(), StandardColumnType.DATETIME.getName())
                .setComment(last_modify_time.getComment()).setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
        table.addColumn(DefaultColumn.last_modify_by.name(), StandardColumnType.VARCHAR.getName()).setPrecision(64)
                .setComment(DefaultColumn.last_modify_by.getComment()).setDefaultValue("");
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
        builder.append(" WHERE 1 = 1");
        if (formListQuery.getFormId() != null) {
            builder.append(" AND ").append(FormConstants.INPUT_FORM_ID).append(" = ?");
        }
        builder.append(" AND ").append(DefaultColumn.status.name()).append(" = ").append(DefaultColumn.Status_NORMAL);
        builder.append(" AND ").append(create_by.name()).append(" = ").append("?");
        builder.append(" LIMIT ? OFFSET ?");
        return builder.toString();
    }

    public String buildCountDsl(FormListQuery formListQuery, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append("COUNT(*) ");
        builder.append(" FROM ").append(getTableName(name));
        builder.append(" WHERE 1 = 1");
        if (formListQuery.getFormId() != null) {
            builder.append(" AND ").append(FormConstants.INPUT_FORM_ID).append(" = ?");
        }
        builder.append(" AND ").append(DefaultColumn.status.name()).append(" = ").append(DefaultColumn.Status_NORMAL);
        builder.append(" AND ").append(create_by.name()).append(" = ").append("?");
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
        return Arrays.asList(create_time.name(), create_by.name(), last_modify_time.name(), last_modify_by.name());
    }

    private List<String> getModifyDefaultColumns() {
        return Arrays.asList(last_modify_time.name(), last_modify_by.name());
    }

    private String getTableName(String name) {
        return tablePrefix + name;
    }

    public String buildSearchFormData(FormDef formDef) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT *" + " FROM ").append(getTableName(formDef.getName())).append(" WHERE ").append(FormConstants.INPUT_FORM_VALUE_ID).append(" = ?");
        builder.append(" AND ").append(DefaultColumn.status.name()).append(" = ").append(DefaultColumn.Status_NORMAL);
        builder.append(" AND ").append(create_by.name()).append(" = ").append("?");
        return builder.toString();
    }

    public String buildUpdateFormData(FormDef formDef, Collection<FormDef.Item> items, Map<String, Object> formData, List<Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(getTableName(formDef.getName())).append(" SET ");
        items.forEach(item -> {
            Object value = formData.get(item.getName());
            if (value != null) {
                builder.append(item.getName()).append(" = ?").append(", ");
                params.add(value);
            }
        });
        getModifyDefaultColumns().forEach(col -> builder.append(col).append(" = ?").append(", "));
        params.add(formData.get(last_modify_time.name()));
        params.add(formData.get(last_modify_by.name()));
        Object status = formData.get(DefaultColumn.status.name());
        if (status != null) {
            builder.append(DefaultColumn.status.name()).append(" = ?");
            params.add(status);
        } else {
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(" WHERE ").append(FormConstants.INPUT_FORM_VALUE_ID).append(" = ?");
        builder.append(" AND ").append(DefaultColumn.status.name()).append(" = ").append(DefaultColumn.Status_NORMAL);
        builder.append(" AND ").append(create_by.name()).append(" = ").append("?");
        return builder.toString();
    }
}
