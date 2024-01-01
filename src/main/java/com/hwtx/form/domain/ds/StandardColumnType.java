package com.hwtx.form.domain.ds;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hwtx.form.domain.ds.entity.geometry.*;
import com.hwtx.form.domain.ds.metadata.BeanUtil;
import com.hwtx.form.domain.ds.metadata.ColumnType;
import com.hwtx.form.domain.ds.metadata.DatabaseType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import static com.hwtx.form.domain.ds.metadata.DatabaseType.*;

public enum StandardColumnType implements ColumnType {

    /* *****************************************************************************************************************
     *
     * 													SQL DATA TYPE
     *
     * =================================================================================================================
     * String
     * String-format
     * number-int/long
     * number-double/float
     * date
     * byte[]
     * byte[]-file
     * byte[]-geometry
     *
     ******************************************************************************************************************/

    /* *****************************************************************************************************************
     *
     *                                              String
     *
     * ****************************************************************************************************************/
    /**
     * MYSQL, pg
     */
    CHAR("CHAR", new DatabaseType[]{MYSQL, PostgreSQL, Informix, HANA, Derby}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (value instanceof String) {
            } else if (value instanceof Date) {
                value = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss");
            } else {
                value = value.toString();
            }
            if (!placeholder) {
                value = "'" + value + "'";
            }
            return value;
        }
    }
    /**
     * oracle, MSSQL
     */
    , NCHAR("NCHAR", new DatabaseType[]{ORACLE, MSSQL, Informix}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , CLOB("CLOB", new DatabaseType[]{ORACLE, Informix, Derby, KingBase}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , NCLOB("NCLOB", new DatabaseType[]{ORACLE, HANA}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , NVARCHAR("NVARCHAR", new DatabaseType[]{MSSQL, Informix, HANA, KingBase}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , NVARCHAR2("NVARCHAR2", new DatabaseType[]{ORACLE}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , LONGTEXT("LONGTEXT", new DatabaseType[]{MYSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , MEDIUMTEXT("MEDIUMTEXT", new DatabaseType[]{MYSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL, pg, sqlite
     */
    , TEXT("TEXT", new DatabaseType[]{MYSQL, PostgreSQL, SQLite, Informix, IoTDB, KingBase}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , NTEXT("NTEXT", new DatabaseType[]{MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , TINYTEXT("TINYTEXT", new DatabaseType[]{MYSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL, pg, oracle, Informix(长度不超过 255 )
     */
    , VARCHAR("VARCHAR", new DatabaseType[]{MYSQL, PostgreSQL, ORACLE, Informix, HANA, Derby, KingBase}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }, LVARCHAR("LVARCHAR", new DatabaseType[]{Informix}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , VARCHAR2("VARCHAR2", new DatabaseType[]{ORACLE}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SYSNAME("SYSNAME", new DatabaseType[]{MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , UUID("UUID", new DatabaseType[]{PostgreSQL, KingBase}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (null != value) {
                value = java.util.UUID.fromString(value.toString());
            }
            if (null == value) {
                value = def;
            }
            return value;
        }
    }
    /**
     * MSSQL
     */
    , UNIQUEIDENTIFIER("UNIQUEIDENTIFIER", new DatabaseType[]{MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL(byte[]), MSSQL
     */
    , BINARY("BINARY", new DatabaseType[]{MYSQL, MSSQL, HANA, ElasticSearch}, byte[].class, false, true)
    /**
     * MYSQL(byte[]), MSSQL
     */
    , VARBINARY("VARBINARY", new DatabaseType[]{MYSQL, MSSQL, HANA}, byte[].class, false, true)


    /* *****************************************************************************************************************
     *
     *                                              String-format
     *
     * ****************************************************************************************************************/

    /**
     * MYSQL, pg
     */
    , JSON("JSON", new DatabaseType[]{MYSQL, PostgreSQL, KingBase}, String.class, true, true) {
        @Override
        public Object convert(Object value, Class target, Object def) {
            if (null == value) {
                return def;
            }
            Class transfer = transfer();
            Class compatible = compatible();
            try {
                if (null == target) {
                    JsonNode node = BeanUtil.JSON_MAPPER.readTree(value.toString());
                    if (node.isArray()) {
//                        value = DataSet.parseJson(node);
                    } else {
//                        value = DataRow.parseJson(node);
                    }
                } else {
                    value = super.convert(value, target, def);
                }
            } catch (Exception e) {
                //不能转成DataSet的List
                value = super.convert(value, target, def);
            }
            return value;
        }
    }

    /**
     * MSSQL
     */
    , XML("XML", new DatabaseType[]{MSSQL, KingBase}, String.class, true, true) {}
    /* *****************************************************************************************************************
     *
     *                                              number-int/long
     *
     * ****************************************************************************************************************/
    /**
     * MYSQL(Boolean), pg(Boolean), MSSQL
     */
    , BIT("BIT", new DatabaseType[]{MYSQL, MSSQL}, Boolean.class, true, true)
    /**
     * pg
     */
    , VARBIT("VARBIT", new DatabaseType[]{PostgreSQL}, Byte[].class, true, true), SHORT("SHORT", new DatabaseType[]{}, Short.class, true, true)

    /**
     * MYSQL, MSSQL, kingbase
     */
    , INT("INT", new DatabaseType[]{MYSQL, MSSQL, Informix, Derby}, Integer.class, true, true)
    /**
     * IoTDB
     */
    , INT32("INT32", new DatabaseType[]{IoTDB}, Integer.class, true, true)
    /**
     * IoTDB
     */
    , INT64("INT64", new DatabaseType[]{IoTDB}, Integer.class, true, true)
    /**
     * Informix
     */
    , INFORMIX_INTEGER("INTEGER", new DatabaseType[]{Informix}, Integer.class, true, true)
    /**
     * oracle
     */
    , LONG("LONG", new DatabaseType[]{ORACLE, ElasticSearch}, String.class, true, true) {}
    /**
     * pg, informix
     */
    , SERIAL("SERIAL", new DatabaseType[]{PostgreSQL, Informix}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL2("SERIAL2", new DatabaseType[]{PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL4("SERIAL4", new DatabaseType[]{PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL8("SERIAL8", new DatabaseType[]{PostgreSQL, Informix}, Long.class, true, true)
    /**
     * pg
     */
    , SMALLSERIAL("SERIAL2", new DatabaseType[]{PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , BIGSERIAL("SERIAL8", new DatabaseType[]{PostgreSQL, Informix}, Long.class, true, true)
    /**
     * pg
     */
    , INT2("INT2", new DatabaseType[]{PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , INT4("INT4", new DatabaseType[]{PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , INT8("INT8", new DatabaseType[]{PostgreSQL, Informix}, Long.class, true, true)
    /**
     * MYSQL
     */
    , BIGINT("BIGINT", new DatabaseType[]{MYSQL, Informix, HANA, Derby, KingBase}, Long.class, true, true)
    /**
     * MYSQL
     */
    , MEDIUMINT("MEDIUMINT", new DatabaseType[]{MYSQL}, Integer.class, true, true)
    /**
     * MYSQL, sqlite
     */
    , INTEGER("INTEGER", new DatabaseType[]{MYSQL, SQLite, HANA, ElasticSearch, Derby, KingBase}, Integer.class, true, true)
    /**
     * MYSQL
     */
    , SMALLINT("SMALLINT", new DatabaseType[]{MYSQL, Informix, HANA, Derby, KingBase}, Integer.class, true, true)
    /**
     * MYSQL
     */
    , TINYINT("TINYINT", new DatabaseType[]{MYSQL, HANA, KingBase}, Integer.class, true, true) {
    }
    /**
     * pg
     */
    , BOOLEAN("BOOLEAN", new DatabaseType[]{PostgreSQL, Informix, HANA, ElasticSearch, KingBase}, Boolean.class, true, true)
    /**
     * pg
     */
    , BOOL("BOOLEAN", new DatabaseType[]{PostgreSQL}, Boolean.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BOOLEAN.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              number-double/float
     *
     * ****************************************************************************************************************/

    //
    /**
     * MYSQL
     */
    , DOUBLE("DOUBLE", new DatabaseType[]{MYSQL, Informix, HANA, IoTDB, ElasticSearch, Derby}, Double.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            Double result = BasicUtil.parseDouble(value, null);
            if (null != def && null == result) {
                result = BasicUtil.parseDouble(def, null);
            }
            return result;
        }
    }
    /**
     * MYSQL(p, s)
     * pg:
     * informix(p)
     * oracle(p)
     * MYSQL, , oracle(BigDecimal)
     */
    , FLOAT_MySQL("FLOAT", new DatabaseType[]{MYSQL}, Float.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            Float result = BasicUtil.parseFloat(value, null);
            if (null != def && null == result) {
                result = BasicUtil.parseFloat(def, null);
            }
            return result;
        }
    }, FLOAT_INFORMIX("FLOAT", new DatabaseType[]{Informix}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, FLOAT_ORACLE("FLOAT", new DatabaseType[]{ORACLE}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, SMALLFLOAT("SMALLFLOAT", new DatabaseType[]{Informix}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * ms
     */
    , FLOAT_MSSQL("FLOAT", new DatabaseType[]{MSSQL}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , FLOAT4("FLOAT4", new DatabaseType[]{PostgreSQL}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, FLOAT("FLOAT", new DatabaseType[]{IoTDB, ElasticSearch, Derby}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , FLOAT8("FLOAT8", new DatabaseType[]{PostgreSQL}, Double.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , BINARY_DOUBLE("BINARY_DOUBLE", new DatabaseType[]{ORACLE}, Double.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , BINARY_FLOAT("BINARY_FLOAT", new DatabaseType[]{ORACLE}, Float.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL(Double), sqlite
     */
    , REAL("REAL", new DatabaseType[]{MYSQL, SQLite, Informix, HANA, Derby, KingBase}, Double.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              byte[]
     *
     * ****************************************************************************************************************/
    /**
     * MYSQL(byte[]), , oracle, sqlite
     */
    , BLOB("BLOB", new DatabaseType[]{MYSQL, ORACLE, SQLite, Informix, HANA, Derby, KingBase}, byte[].class, true, true) {
        public Object read(Object value, Object def, Class clazz) {
            if (clazz == byte[].class) {

            } else if (clazz == String.class) {
                value = new String((byte[]) value);
            }
            return value;
        }

        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (value instanceof byte[]) {

            } else {
                if (value instanceof String) {
                    String str = (String) value;
                    if (Base64Util.verify(str)) {
                        try {
                            value = Base64Util.decode(str);
                        } catch (Exception e) {
                            value = str.getBytes();
                        }
                    } else {
                        value = str.getBytes();
                    }
                }
            }
            return value;
        }
    }
    /**
     * MYSQL
     */
    , LONGBLOB("LONGBLOB", new DatabaseType[]{MYSQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , MEDIUMBLOB("MEDIUMBLOB", new DatabaseType[]{MYSQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , TINYBLOB("TINYBLOB", new DatabaseType[]{MYSQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * MYSQL
     */
    , MULTILINESTRING("MULTILINESTRING", new DatabaseType[]{MYSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , BYTEA("BYTEA", new DatabaseType[]{PostgreSQL}, byte[].class, true, true), BYTE("BYTE", new DatabaseType[]{Informix, ElasticSearch}, byte[].class, true, true)
    /**
     * pg
     */
    , JSONB("JSONB", new DatabaseType[]{PostgreSQL, KingBase}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              byte[]-file
     *
     * ****************************************************************************************************************/
    /**
     * MSSQL
     */
    , IMAGE("IMAGE", new DatabaseType[]{MSSQL}, byte[].class, true, true)
    /**
     * oracle
     */
    , BFILE("BFILE", new DatabaseType[]{ORACLE, KingBase}, byte[].class, true, true)
    /* *****************************************************************************************************************
     *
     *                                              byte[]-geometry
     *
     * ****************************************************************************************************************/
    /**
     * MYSQL, pg
     */
    , POINT("POINT", new DatabaseType[]{MYSQL, PostgreSQL, KingBase}, Point.class, byte[].class, true, true) {
        public Object read(Object value, Object def, Class clazz) {
            if (null == value) {
                return value;
            }
            return value;
        }

        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (value instanceof byte[]) {
                return value;
            }
            return value;
        }
    }, ST_POINT("ST_POINT", new DatabaseType[]{MYSQL, PostgreSQL}, Point.class, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return POINT.write(value, def, placeholder);
        }
    }, GEOGRAPHY_POINT("GEOGRAPHY_POINT", new DatabaseType[]{VoltDB}, Point.class, byte[].class, true, true)
    /**
     * MYSQL
     */
    , MULTIPOLYGON("MULTIPOLYGON", new DatabaseType[]{MYSQL}, MultiPolygon.class, byte[].class, true, true)
    /**
     * MYSQL
     */
    , MULTIPOINT("MULTIPOINT", new DatabaseType[]{MYSQL}, MultiPoint.class, byte[].class, true, true)
    /**
     * MYSQL, pg
     */
    , POLYGON("POLYGON", new DatabaseType[]{MYSQL, PostgreSQL, KingBase}, Polygon.class, byte[].class, true, true)
    /**
     * MYSQL
     */
    , GEOMETRY("GEOMETRY", new DatabaseType[]{MYSQL}, byte[].class, true, true), ST_GEOMETRY("ST_GEOMETRY", new DatabaseType[]{HANA}, byte[].class, true, true)
    /**
     * MYSQL
     */
    , GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", new DatabaseType[]{MYSQL}, byte[].class, true, true)
    /**
     * MSSQL
     */
    , HIERARCHYID("HIERARCHYID", new DatabaseType[]{MSSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , LINE("LINE", new DatabaseType[]{PostgreSQL, KingBase}, LineString.class, byte[].class, true, true)
    /**
     * MYSQL
     */
    , LINESTRING("LINESTRING", new DatabaseType[]{MYSQL}, LineString.class, byte[].class, true, true)
    /**
     * pg
     */
    , PATH("PATH", new DatabaseType[]{PostgreSQL, KingBase}, LineString.class, true, true)
    /**
     * pg
     */
    , LSEG("LSEG", new DatabaseType[]{PostgreSQL, KingBase}, byte[].class, true, true)
    /**
     * MSSQL
     */
    , GEOGRAPHY("GEOGRAPHY", new DatabaseType[]{MSSQL, PostgreSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , BOX("BOX", new DatabaseType[]{PostgreSQL, KingBase}, byte[].class, true, true)
    /**
     * pg
     */
    , CIDR("CIDR", new DatabaseType[]{PostgreSQL, KingBase}, byte[].class, true, true)
    /**
     * pg
     */
    , CIRCLE("CIRCLE", new DatabaseType[]{PostgreSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , INET("INET", new DatabaseType[]{PostgreSQL, KingBase}, byte[].class, true, true)


    /* *****************************************************************************************************************
     *
     *                                              待实现
     *
     * ****************************************************************************************************************/

    /**
     * MYSQL
     */
    , ENUM("ENUM", new DatabaseType[]{MYSQL}, String.class, true, true)
    /**
     * pg
     */
    , INTERVAL("INTERVAL", new DatabaseType[]{PostgreSQL, Informix}, null, true, true)
    /**
     * oracle
     */
    , RAW("RAW", new DatabaseType[]{ORACLE}, byte[].class, true, true)
    /**
     * oracle
     */
    , ROWID("ROWID", new DatabaseType[]{ORACLE}, null, true, true)
    /**
     * MYSQL
     */
    , SET("SET", new DatabaseType[]{MYSQL}, String.class, true, true)
    /**
     * pg
     */
    , TSQUERY("TSQUERY", new DatabaseType[]{PostgreSQL, KingBase}, null, true, true)
    /**
     * pg
     */
    , TSVECTOR("TSVECTOR", new DatabaseType[]{PostgreSQL, KingBase}, null, true, true)
    /**
     * pg
     */
    , MACADDR("MACADDR", new DatabaseType[]{PostgreSQL, KingBase}, null, true, true)
    /**
     * pg
     */
    , PG_SNAPSHOT("PG_SNAPSHOT", new DatabaseType[]{PostgreSQL}, null, true, true)
    /**
     * pg
     * 弃用 换成pg_snapshot
     */
    , TXID_SNAPSHOT("TXID_SNAPSHOT", new DatabaseType[]{PostgreSQL, KingBase}, null, true, true)
    /**
     * oracle
     */
    , UROWID("UROWID", new DatabaseType[]{ORACLE}, null, true, true)
    /**
     * MSSQL
     */
    , SQL_VARIANT("SQL_VARIANT", new DatabaseType[]{MSSQL}, null, true, true), KEYWORD("KEYWORD", new DatabaseType[]{ElasticSearch}, null, true, true), OBJECT("OBJECT", new DatabaseType[]{ElasticSearch}, null, true, true);
    private final DatabaseType[] dbs;
    private final String name;
    private Class transfer; //中间转换类型 转换成其他格式前先转换成transfer类型
    private final Class compatible; //从数据库中读写数据的类型
    private final Boolean ignorePrecision;
    private final Boolean ignoreScale;
    private boolean array;

    StandardColumnType(String name, DatabaseType[] dbs, Class transfer, Class compatible, Boolean ignorePrecision, Boolean ignoreScale) {
        this.name = name;
        this.dbs = dbs;
        this.transfer = transfer;
        this.compatible = compatible;
        this.ignorePrecision = ignorePrecision;
        this.ignoreScale = ignoreScale;
    }

    StandardColumnType(String name, DatabaseType[] dbs, Class compatible, Boolean ignorePrecision, Boolean ignoreScale) {
        this.name = name;
        this.dbs = dbs;
        this.compatible = compatible;
        this.ignorePrecision = ignorePrecision;
        this.ignoreScale = ignoreScale;
    }

    @Override
    public Object convert(Object value, Object def) {
        return convert(value, null, def);
    }

    @Override
    public Object convert(Object value, Class target, boolean array) {
        Object def = null;
        return convert(value, target, array, def);
    }

    @Override
    public Object convert(Object value, Class target, boolean array, Object def) {
        if (null == target) {
            target = compatible;
        }
        if (null != value) {
            if (value.getClass() == target) {
                return value;
            }
        }
        return value;
    }

    @Override
    public Object convert(Object value, Object obj, Field field) {
        return convert(value, field.getType());
    }

    /**
     * 以String类型拼接SQL需要引号或类型转换函数
     *
     * @param value       value
     * @param def         def
     * @param placeholder 是否需要占位符
     * @return Object
     */
    @Override
    public Object write(Object value, Object def, boolean array, boolean placeholder) {
        if (null != value) {
            if (compatible == String.class && !placeholder) {
                value = "'" + value + "'";
            }
        }
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean ignorePrecision() {
        return ignorePrecision;
    }

    @Override
    public boolean ignoreScale() {
        return ignoreScale;
    }

    @Override
    public boolean support() {
        return true;
    }

    @Override
    public Class compatible() {
        return compatible;
    }

    @Override
    public Class transfer() {
        return transfer;
    }

    @Override
    public DatabaseType[] dbs() {
        return dbs;
    }

    @Override
    public boolean isArray() {
        return array;
    }

    @Override
    public void setArray(boolean array) {
        this.array = array;
    }
}
