package com.hwtx.form.domain;

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
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class MetadataRepo {

    @Resource
    private DatasourceDao datasourceDao;

    public boolean create(String formName, List<FormDef.Item> itemItems) {
        Table table = new Table(formName);
        if (!datasourceDao.exists(table)) {
            table.addColumn("id", StandardColumnType.BIGINT.getName()).primary(true).autoIncrement(true).setComment("主键").setNullable(false);
            itemItems.forEach(item -> {
                FormDef.ValidationsDef validationsDef = item.getValidationsDef();
                Column column;
                if (BooleanUtils.isTrue(validationsDef.getIsNumeric()) || Objects.equals(FormItemType.INPUT_NUMBER.getType(), item.getType())) {
                    if (item.getPrecision() != null) {
                        column = table.addColumn(item.getName(), StandardColumnType.DECIMAL.getName()).setPrecision(10, item.getPrecision()).setComment(item.getLabel());
                    } else {
                        column = table.addColumn(item.getName(), StandardColumnType.INT.getName()).setComment(item.getLabel());
                    }
                } else if (BooleanUtils.isTrue(validationsDef.getIsAlpha())) {
                    int length = 200;
                    if (validationsDef.getMaxLength() != null) {
                        length = validationsDef.getMaxLength();
                    }
                    if (validationsDef.getMinLength() != null) {
                        length += validationsDef.getMinLength();
                    }
                    column = table.addColumn(item.getName(), StandardColumnType.VARCHAR.getName()).setPrecision(length).setComment(item.getLabel());
                } else {
                    column = table.addColumn(item.getName(), StandardColumnType.VARCHAR.getName()).setPrecision(200).setComment(item.getLabel());
                }
                if (BooleanUtils.isTrue(item.getRequired())) {
                    column.setNullable(false);
                }
            });
            table.addColumn("status", StandardColumnType.TINYINT.getName()).setPrecision(1)
                    .setComment("逻辑删除 1:正常  0:删除").setNullable(true).setDefaultValue(1);
            table.addColumn("create_time", StandardColumnType.DATETIME.getName())
                    .setComment("创建时间").setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
            table.addColumn("create_by", StandardColumnType.VARCHAR.getName()).setPrecision(64).setComment("创建者").setDefaultValue("");
            table.addColumn("last_modify_time", StandardColumnType.DATETIME.getName())
                    .setComment("最后修改时间").setNullable(false).setDefaultValue(JDBCAdapter.SQL_BUILD_IN_VALUE.CURRENT_TIMESTAMP);
            table.addColumn("last_modify_by", StandardColumnType.VARCHAR.getName()).setPrecision(64).setComment("修改人").setDefaultValue("");
            try {
                return datasourceDao.create(table);
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException("创建表单数据表失败");
            }
        }
        return false;
    }
}
