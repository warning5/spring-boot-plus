package com.hwtx.form.persistence.ds.mysql;

import com.hwtx.form.persistence.ds.DataRuntime;
import com.hwtx.form.persistence.ds.DefaultJDBCAdapter;
import com.hwtx.form.persistence.ds.Run;
import com.hwtx.form.persistence.ds.SimpleRun;
import com.hwtx.form.persistence.ds.metadata.*;
import com.hwtx.form.util.BasicUtil;

import java.util.*;

public class MySQLGenusAdapter extends DefaultJDBCAdapter {

    @Override
    public String version() {
        return null;
    }

    @Override
    public DatabaseType compatible() {
        return null;
    }

    /**
     * catalog[命令合成]<br/>
     * 查询所有数据库
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param name    名称统配符或正则
     * @param greedy  贪婪模式 true:查询权限范围内尽可能多的数据
     * @return sqls
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildQuerySchemaRun(DataRuntime runtime, boolean greedy, Catalog catalog, String name) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("SHOW SCHEMAS");
        if (BasicUtil.isNotEmpty(name)) {
            builder.append(" LIKE '").append(name).append("'");
        }
        return runs;
    }


    /**
     * table[命令合成]<br/>
     * 查询表,不是查表中的数据
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param catalog catalog
     * @param schema  schema
     * @param pattern 名称统配符或正则
     * @param types   "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     * @return String
     */
    @Override
    public List<Run> buildQueryTablesRun(DataRuntime runtime, Catalog catalog, Schema schema, String pattern, String types) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();

        builder.append("SELECT * FROM information_schema.TABLES WHERE 1=1 ");
        // 8.0版本中 这个表中 TABLE_CATALOG = def  TABLE_SCHEMA = 数据库名
		/*if(BasicUtil.isNotEmpty(catalog)){
			builder.append(" AND TABLE_SCHEMA = '").append(catalog).append("'");
		}*/
        if (BasicUtil.isNotEmpty(schema)) {
            builder.append(" AND TABLE_SCHEMA = '").append(schema.getName()).append("'");
        }
        if (BasicUtil.isNotEmpty(pattern)) {
            builder.append(" AND TABLE_NAME LIKE '").append(objectName(runtime, pattern)).append("'");
        }
        if (BasicUtil.isNotEmpty(types)) {
            String[] tmps = types.split(",");
            builder.append(" AND TABLE_TYPE IN(");
            int idx = 0;
            for (String tmp : tmps) {
                if (idx > 0) {
                    builder.append(",");
                }
                if (tmp.equalsIgnoreCase("table")) {
                    tmp = "BASE TABLE";
                }
                builder.append("'").append(tmp).append("'");
                idx++;
            }
            builder.append(")");
        } else {
            builder.append(" AND TABLE_TYPE IN ('BASE TABLE','TABLE')");
        }
        return runs;
    }

    /**
     * column[命令合成]<br/>
     * 查询表上的列
     *
     * @param runtime  运行环境主要包含驱动适配器 数据源或客户端
     * @param table    表
     * @param metadata 是否根据metadata(true:SELECT * FROM T WHERE 1=0,false:查询系统表)
     * @return sqls
     */
    @Override
    public List<Run> buildQueryColumnRun(DataRuntime runtime, Table table, boolean metadata) throws Exception {
        List<Run> runs = new ArrayList<>();
        Schema schema = null;
        String name = null;
        if (null != table) {
            name = table.getName();
            schema = table.getSchema();
        }
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (metadata) {
            builder.append("SELECT * FROM ");
            assert table != null;
            name(runtime, builder, table);
            builder.append(" WHERE 1=0");
        } else {
            builder.append("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE 1=1 ");
			/*if(BasicUtil.isNotEmpty(catalog)){
				builder.append(" AND TABLE_CATALOG = '").append(catalog).append("'");
			}*/
            if (BasicUtil.isNotEmpty(schema)) {
                builder.append(" AND TABLE_SCHEMA = '").append(schema.getName()).append("'");
            }
            if (BasicUtil.isNotEmpty(name)) {
                builder.append(" AND TABLE_NAME = '").append(objectName(runtime, name)).append("'");
            }
            builder.append(" ORDER BY TABLE_NAME, ORDINAL_POSITION");
        }
        return runs;
    }

    /**
     * primary[命令合成]<br/>
     * 查询表上的主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @return sqls
     */
    @Override
    public List<Run> buildQueryPrimaryRun(DataRuntime runtime, Table table) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("SHOW INDEX FROM ");
        name(runtime, builder, table);

        return runs;
    }

    /**
     * index[命令合成]<br/>
     * 查询表上的索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param name    名称
     * @return sqls
     */
    @Override
    public List<Run> buildQueryIndexRun(DataRuntime runtime, Table table, String name) {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("SELECT * FROM INFORMATION_SCHEMA.STATISTICS\n");
        builder.append("WHERE 1=1\n");
        if (null != table) {
            if (null != table.getSchema()) {
                builder.append("AND TABLE_SCHEMA='").append(table.getSchema()).append("'\n");
            }
            if (null != table.getName()) {
                builder.append("AND TABLE_NAME='").append(objectName(runtime, table.getName())).append("'\n");
            }
        }
        if (BasicUtil.isNotEmpty(name)) {
            builder.append("AND INDEX_NAME='").append(name).append("'\n");
        }
        builder.append("ORDER BY SEQ_IN_INDEX");
        return runs;
    }

    /**
     * table[命令合成]<br/>
     * 修改列
     * 有可能生成多条SQL,根据数据库类型优先合并成一条执行
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param table   表
     * @param columns 列
     * @return List
     */
    @Override
    public List<Run> buildAlterRun(DataRuntime runtime, Table table, Collection<Column> columns) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        if (!columns.isEmpty()) {
            StringBuilder builder = run.getBuilder();
            List<Run> slices = new ArrayList<>();
            for (Column column : columns) {
                ACTION.DDL action = column.getAction();
                if (action == ACTION.DDL.COLUMN_ADD) {
                    slices.addAll(buildAddRun(runtime, column, true));
                } else if (action == ACTION.DDL.COLUMN_ALTER) {
                    slices.addAll(buildAlterRun(runtime, column));
                } else if (action == ACTION.DDL.COLUMN_DROP) {
                    slices.addAll(buildDropRun(runtime, column, true));
                }
            }
            boolean first = true;
            for (Run slice : slices) {
                if (BasicUtil.isNotEmpty(slice)) {
                    builder.append("\n");
                    if (!first) {
                        builder.append(",");
                    }
                    first = false;
                    builder.append(slice.getFinalUpdate().trim());
                }
            }
        }
        return runs;
    }

    /**
     * table[命令合成]<br/>
     * 重命名
     * 子类实现
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildRenameRun(DataRuntime runtime, Table meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("RENAME TABLE ");
        name(runtime, builder, meta);
        builder.append(" TO ");
        name(runtime, builder, meta.getUpdate());
        return runs;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 添加表备注(表创建完成后调用,创建过程能添加备注的不需要实现)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildAppendCommentRun(DataRuntime runtime, Table meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        String comment = meta.getComment();
        if (BasicUtil.isEmpty(comment)) {
            return runs;
        }
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("ALTER TABLE ");
        name(runtime, builder, meta);
        builder.append(" COMMENT '").append(comment).append("'");
        return runs;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 修改备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    表
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildChangeCommentRun(DataRuntime runtime, Table meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        String comment = meta.getComment();
        if (BasicUtil.isEmpty(comment)) {
            return runs;
        }
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("ALTER TABLE ");
        name(runtime, builder, meta);
        builder.append(" COMMENT '").append(comment).append("'");
        return runs;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 定义表的主键标识,在创建表的DDL结尾部分(注意不要跟列定义中的主键重复)
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    表
     * @return StringBuilder
     */
    @Override
    public StringBuilder primary(DataRuntime runtime, StringBuilder builder, Table meta) {
        PrimaryKey primary = meta.getPrimaryKey();
        Map<String, Column> pks;
        if (null != primary) {
            pks = primary.getColumns();
        } else {
            pks = meta.primarys();
        }
        if (!pks.isEmpty()) {
            builder.append(",PRIMARY KEY (");
            boolean first = true;
            for (Column pk : pks.values()) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                delimiter(builder, pk.getName());
                String order = pk.getOrder();
                if (BasicUtil.isNotEmpty(order)) {
                    builder.append(" ").append(order);
                }
            }
            builder.append(")");
        }
        return builder;
    }

    /**
     * table[命令合成-子流程]<br/>
     * 编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    表
     * @return StringBuilder
     */
    @Override
    public StringBuilder charset(DataRuntime runtime, StringBuilder builder, Table meta) {
        String charset = meta.getCharset();
        String collate = meta.getCollate();
        if (BasicUtil.isNotEmpty(charset)) {
            builder.append(" CHARSET = ").append(charset);
        }
        if (BasicUtil.isNotEmpty(collate)) {
            builder.append(" COLLATE = ").append(collate);
        }
        return builder;
    }

    /**
     * column[命令合成]<br/>
     * 添加列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, Column meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (!slice) {
            Table table = meta.getTable(true);
            builder.append("ALTER TABLE ");
            name(runtime, builder, table);
        }
        Column update = meta.getUpdate();
        if (null == update) {
            // 添加列
            addColumnGuide(runtime, builder, meta);
            delimiter(builder, meta.getName()).append(" ");
            // 数据类型
            type(runtime, builder, meta);
            // 编码
            charset(runtime, builder, meta);
            // 默认值
            defaultValue(runtime, builder, meta);
            // 非空
            nullable(runtime, builder, meta);
            // 更新事件
            onupdate(runtime, builder, meta);
            // 备注
            comment(runtime, builder, meta);
        }

        runs.addAll(buildAppendCommentRun(runtime, meta));
        return runs;
    }

    /**
     * column[命令合成]<br/>
     * 修改列
     * 修改列 ALTER TABLE   HR_USER CHANGE UPT_TIME UPT_TIME datetime   DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP  comment '修改时间' AFTER ID;
     * 有可能生成多条SQL
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return List
     */
    @Override
    public List<Run> buildAlterRun(DataRuntime runtime, Column meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        Table table = meta.getTable(true);
        builder.append("ALTER TABLE ");
        name(runtime, builder, table);
        Column update = meta.getUpdate();
        if (null != update) {
            if (!Objects.equals(update.getName(), meta.getName())) {
                builder.append(" CHANGE ");
                delimiter(builder, meta.getName()).append(" ");
                delimiter(builder, update.getName()).append(" ");
            } else {
                builder.append(" MODIFY ");
                delimiter(builder, meta.getName()).append(" ");
            }
            define(runtime, builder, update);
        }
        return runs;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 修改表的关键字
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @return String
     */
    @Override
    public String alterColumnKeyword(DataRuntime runtime) {
        return "ALTER COLUMN ";
    }

    /**
     * column[命令合成-子流程]<br/>
     * 取消自增
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    列
     * @return sql
     * @throws Exception 异常
     */
    @Override
    public List<Run> buildDropAutoIncrement(DataRuntime runtime, Column meta) throws Exception {
        meta.update().autoIncrement(false);
        return buildAlterRun(runtime, meta);
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略长度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return Boolean 检测不到时返回null
     */
    @Override
    public Boolean checkIgnorePrecision(DataRuntime runtime, String type) {
        type = type.toUpperCase();
        if (type.contains("INT")) {
            return false;
        }
        if (type.contains("DATE")) {
            return true;
        }
        if (type.contains("TIME")) {
            return true;
        }
        if (type.contains("YEAR")) {
            return true;
        }
        if (type.contains("TEXT")) {
            return true;
        }
        if (type.contains("BLOB")) {
            return true;
        }
        if (type.contains("JSON")) {
            return true;
        }
        if (type.contains("POINT")) {
            return true;
        }
        if (type.contains("LINE")) {
            return true;
        }
        if (type.contains("POLYGON")) {
            return true;
        }
        if (type.contains("GEOMETRY")) {
            return true;
        }
        return null;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:是否忽略精度
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param type    列数据类型
     * @return Boolean 检测不到时返回null
     */
    @Override
    public Boolean checkIgnoreScale(DataRuntime runtime, String type) {
        type = type.toUpperCase();
        if (type.contains("INT")) {
            return true;
        }
        if (type.contains("DATE")) {
            return true;
        }
        if (type.contains("TIME")) {
            return true;
        }
        if (type.contains("YEAR")) {
            return true;
        }
        if (type.contains("TEXT")) {
            return true;
        }
        if (type.contains("BLOB")) {
            return true;
        }
        if (type.contains("JSON")) {
            return true;
        }
        if (type.contains("POINT")) {
            return true;
        }
        if (type.contains("LINE")) {
            return true;
        }
        if (type.contains("POLYGON")) {
            return true;
        }
        if (type.contains("GEOMETRY")) {
            return true;
        }
        return null;
    }


    /**
     * column[命令合成-子流程]<br/>
     * 列定义:编码
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder charset(DataRuntime runtime, StringBuilder builder, Column meta) {
        // CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci
        String typeName = meta.getTypeName();
        if (null != typeName && typeName.toLowerCase().contains("char")) {
            String charset = meta.getCharset();
            if (BasicUtil.isNotEmpty(charset)) {
                builder.append(" CHARACTER SET ").append(charset);
                String collate = meta.getCollate();
                if (BasicUtil.isNotEmpty(collate)) {
                    builder.append(" COLLATE ").append(collate);
                }
            }
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:递增列
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder increment(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (meta.isAutoIncrement() == 1) {
            builder.append(" AUTO_INCREMENT");
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:更新行事件
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder onupdate(DataRuntime runtime, StringBuilder builder, Column meta) {
        if (meta.isOnUpdate() == 1) {
            builder.append(" ON UPDATE CURRENT_TIMESTAMP");
        }
        return builder;
    }

    /**
     * column[命令合成-子流程]<br/>
     * 列定义:备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param builder builder
     * @param meta    列
     * @return StringBuilder
     */
    @Override
    public StringBuilder comment(DataRuntime runtime, StringBuilder builder, Column meta) {
        String comment = meta.getComment();
        if (BasicUtil.isNotEmpty(comment)) {
            builder.append(" COMMENT '").append(comment).append("'");
        }
        return builder;
    }

    /**
     * primary[命令合成]<br/>
     * 添加主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildAddRun(DataRuntime runtime, PrimaryKey meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        Map<String, Column> columns = meta.getColumns();
        if (!columns.isEmpty()) {
            if (!slice) {
                builder.append("ALTER TABLE ");
                name(runtime, builder, meta.getTable(true));
            }
            builder.append(" ADD PRIMARY KEY (");
            boolean first = true;
            for (Column column : columns.values()) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                delimiter(builder, column.getName());
            }
            builder.append(")");
        }
        return runs;
    }


    /**
     * primary[命令合成]<br/>
     * 删除主键
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    主键
     * @param slice   是否只生成片段(不含alter table部分，用于DDL合并)
     * @return String
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, PrimaryKey meta, boolean slice) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        if (!slice) {
            builder.append("ALTER TABLE ");
            name(runtime, builder, meta.getTable(true));
        }
        builder.append(" DROP PRIMARY KEY");
        return runs;
    }

    /**
     * index[命令合成]<br/>
     * 删除索引
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @return String
     */
    @Override
    public List<Run> buildDropRun(DataRuntime runtime, Index meta) throws Exception {
        List<Run> runs = new ArrayList<>();
        Run run = new SimpleRun(runtime);
        runs.add(run);
        StringBuilder builder = run.getBuilder();
        builder.append("ALTER TABLE ");
        name(runtime, builder, meta.getTable(true));
        if (meta.isPrimary()) {
            builder.append(" DROP PRIMARY KEY");
        } else {
            builder.append(" DROP INDEX ").append(meta.getName());
        }
        return runs;
    }

    /**
     * index[命令合成-子流程]<br/>
     * 索引类型
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @param builder builder
     * @return StringBuilder
     */
    @Override
    public StringBuilder type(DataRuntime runtime, StringBuilder builder, Index meta) {
        String type = meta.getType();
        if (BasicUtil.isNotEmpty(type)) {
            builder.append("USING ").append(type).append(" ");
        }
        return builder;
    }

    /**
     * index[命令合成-子流程]<br/>
     * 索引备注
     *
     * @param runtime 运行环境主要包含驱动适配器 数据源或客户端
     * @param meta    索引
     * @param builder builder
     * @return StringBuilder
     */
    @Override
    public StringBuilder comment(DataRuntime runtime, StringBuilder builder, Index meta) {
        String comment = meta.getComment();
        if (BasicUtil.isNotEmpty(comment)) {
            builder.append(" COMMENT '").append(comment).append("'");
        }
        ;
        return builder;
    }

    /**
     * 伪表
     *
     * @return String
     */
    protected String dummy() {
        return super.dummy();
    }

}
