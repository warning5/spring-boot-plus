package com.hwtx.form.persistence.ds;

import com.hwtx.form.persistence.ds.entity.geometry.*;
import com.hwtx.form.persistence.ds.metadata.ColumnType;
import com.hwtx.form.persistence.ds.metadata.DatabaseType;
import com.hwtx.form.util.Base64Util;
import com.hwtx.form.util.BasicUtil;
import com.hwtx.form.util.DateUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public enum StandardColumnType implements ColumnType {


    /**
     * mysql, pg
     */
    CHAR("CHAR", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (value instanceof String) {
            } else if (value instanceof Date) {
                value = DateUtil.format((Date) value);
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
    , NCHAR("NCHAR", new DatabaseType[]{DatabaseType.ORACLE, DatabaseType.MSSQL, DatabaseType.Informix}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , CLOB("CLOB", new DatabaseType[]{DatabaseType.ORACLE, DatabaseType.Informix, DatabaseType.Derby, DatabaseType.KingBase}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , NCLOB("NCLOB", new DatabaseType[]{DatabaseType.ORACLE, DatabaseType.HANA}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , NVARCHAR("NVARCHAR", new DatabaseType[]{DatabaseType.MSSQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.KingBase}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , NVARCHAR2("NVARCHAR2", new DatabaseType[]{DatabaseType.ORACLE}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , LONGTEXT("LONGTEXT", new DatabaseType[]{DatabaseType.MySQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , MEDIUMTEXT("MEDIUMTEXT", new DatabaseType[]{DatabaseType.MySQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql, pg, sqlite
     */
    , TEXT("TEXT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.SQLite, DatabaseType.Informix, DatabaseType.IoTDB, DatabaseType.KingBase}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , NTEXT("NTEXT", new DatabaseType[]{DatabaseType.MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , TINYTEXT("TINYTEXT", new DatabaseType[]{DatabaseType.MySQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql, pg, oracle, Informix(长度不超过 255 )
     */
    , VARCHAR("VARCHAR", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.ORACLE, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby, DatabaseType.KingBase}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }, LVARCHAR("LVARCHAR", new DatabaseType[]{DatabaseType.Informix}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , VARCHAR2("VARCHAR2", new DatabaseType[]{DatabaseType.ORACLE}, String.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SYSNAME("SYSNAME", new DatabaseType[]{DatabaseType.MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , UUID("UUID", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, String.class, true, true) {
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
    , UNIQUEIDENTIFIER("UNIQUEIDENTIFIER", new DatabaseType[]{DatabaseType.MSSQL}, String.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return CHAR.write(value, def, placeholder);
        }
    }
    /**
     * mysql(byte[]), MSSQL
     */
    , BINARY("BINARY", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.MSSQL, DatabaseType.HANA, DatabaseType.ElasticSearch}, byte[].class, false, true)
    /**
     * mysql(byte[]), MSSQL
     */
    , VARBINARY("VARBINARY", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.MSSQL, DatabaseType.HANA}, byte[].class, false, true)


    /* *****************************************************************************************************************
     *
     *                                              number-int/long
     *
     * ****************************************************************************************************************/
    /**
     * mysql(Boolean), pg(Boolean), MSSQL
     */
    , BIT("BIT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.MSSQL}, Boolean.class, true, true)
    /**
     * pg
     */
    , VARBIT("VARBIT", new DatabaseType[]{DatabaseType.PostgreSQL}, Byte[].class, true, true), SHORT("SHORT", new DatabaseType[]{}, Short.class, true, true)

    /**
     * mysql, MSSQL, kingbase
     */
    , INT("INT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.MSSQL, DatabaseType.Informix, DatabaseType.Derby}, Integer.class, true, true)
    /**
     * IoTDB
     */
    , INT32("INT32", new DatabaseType[]{DatabaseType.IoTDB}, Integer.class, true, true)
    /**
     * IoTDB
     */
    , INT64("INT64", new DatabaseType[]{DatabaseType.IoTDB}, Integer.class, true, true)
    /**
     * Informix
     */
    , INFORMIX_INTEGER("INTEGER", new DatabaseType[]{DatabaseType.Informix}, Integer.class, true, true)
    /**
     * oracle
     */
    , LONG("LONG", new DatabaseType[]{DatabaseType.ORACLE, DatabaseType.ElasticSearch}, String.class, true, true) {}
    /**
     * pg, informix
     */
    , SERIAL("SERIAL", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL2("SERIAL2", new DatabaseType[]{DatabaseType.PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL4("SERIAL4", new DatabaseType[]{DatabaseType.PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , SERIAL8("SERIAL8", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix}, Long.class, true, true)
    /**
     * pg
     */
    , SMALLSERIAL("SERIAL2", new DatabaseType[]{DatabaseType.PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , BIGSERIAL("SERIAL8", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix}, Long.class, true, true)
    /**
     * pg
     */
    , INT2("INT2", new DatabaseType[]{DatabaseType.PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , INT4("INT4", new DatabaseType[]{DatabaseType.PostgreSQL}, Integer.class, true, true)
    /**
     * pg
     */
    , INT8("INT8", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix}, Long.class, true, true)
    /**
     * mysql
     */
    , BIGINT("BIGINT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby, DatabaseType.KingBase}, Long.class, true, true)
    /**
     * mysql
     */
    , MEDIUMINT("MEDIUMINT", new DatabaseType[]{DatabaseType.MySQL}, Integer.class, true, true)
    /**
     * mysql, sqlite
     */
    , INTEGER("INTEGER", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.SQLite, DatabaseType.HANA, DatabaseType.ElasticSearch, DatabaseType.Derby, DatabaseType.KingBase}, Integer.class, true, true)
    /**
     * mysql
     */
    , SMALLINT("SMALLINT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby, DatabaseType.KingBase}, Integer.class, true, true)
    /**
     * mysql
     */
    , TINYINT("TINYINT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.HANA, DatabaseType.KingBase}, Integer.class, true, true) {
    }
    /**
     * pg
     */
    , BOOLEAN("BOOLEAN", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.ElasticSearch, DatabaseType.KingBase}, Boolean.class, true, true)
    /**
     * pg
     */
    , BOOL("BOOLEAN", new DatabaseType[]{DatabaseType.PostgreSQL}, Boolean.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BOOLEAN.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              number-double/float
     *
     * ****************************************************************************************************************/

    /**
     * mysql, pg, oracle
     */
    , DECIMAL("DECIMAL", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.ORACLE, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby}, BigDecimal.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            BigDecimal result = BasicUtil.parseDecimal(value, null);
            if (null != def && null == result) {
                result = BasicUtil.parseDecimal(def, null);
            }
            return result;
        }
    }, SMALLDECIMAL("SMALLDECIMAL", new DatabaseType[]{DatabaseType.HANA}, BigDecimal.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DECIMAL.write(value, def, placeholder);
        }
    }
    //
    /**
     * mysql
     */
    , DOUBLE("DOUBLE", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.IoTDB, DatabaseType.ElasticSearch, DatabaseType.Derby}, Double.class, false, false) {
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
     * mysql(p, s)
     * pg:
     * informix(p)
     * oracle(p)
     * mysql, , oracle(BigDecimal)
     */
    , FLOAT_MySQL("FLOAT", new DatabaseType[]{DatabaseType.MySQL}, Float.class, false, false) {
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
    }, FLOAT_INFORMIX("FLOAT", new DatabaseType[]{DatabaseType.Informix}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, FLOAT_ORACLE("FLOAT", new DatabaseType[]{DatabaseType.ORACLE}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, SMALLFLOAT("SMALLFLOAT", new DatabaseType[]{DatabaseType.Informix}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * ms
     */
    , FLOAT_MSSQL("FLOAT", new DatabaseType[]{DatabaseType.MSSQL}, Float.class, false, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , FLOAT4("FLOAT4", new DatabaseType[]{DatabaseType.PostgreSQL}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }, FLOAT("FLOAT", new DatabaseType[]{DatabaseType.IoTDB, DatabaseType.ElasticSearch, DatabaseType.Derby}, Float.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , FLOAT8("FLOAT8", new DatabaseType[]{DatabaseType.PostgreSQL}, Double.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , BINARY_DOUBLE("BINARY_DOUBLE", new DatabaseType[]{DatabaseType.ORACLE}, Double.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , BINARY_FLOAT("BINARY_FLOAT", new DatabaseType[]{DatabaseType.ORACLE}, Float.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DOUBLE.write(value, def, placeholder);
        }
    }
    /**
     * pg
     */
    , MONEY("MONEY", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix, DatabaseType.KingBase}, BigDecimal.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DECIMAL.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SMALLMONEY("SMALLMONEY", new DatabaseType[]{DatabaseType.MSSQL}, BigDecimal.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DECIMAL.write(value, def, placeholder);
        }
    }
    /**
     * mysql, sqlite
     */
    , NUMERIC("NUMERIC", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.SQLite, DatabaseType.Informix, DatabaseType.KingBase}, BigDecimal.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DECIMAL.write(value, def, placeholder);
        }
    }
    /**
     * oracle
     */
    , NUMBER("NUMBER", new DatabaseType[]{DatabaseType.ORACLE}, BigDecimal.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DECIMAL.write(value, def, placeholder);
        }
    }
    /**
     * mysql(Double), sqlite
     */
    , REAL("REAL", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.SQLite, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby, DatabaseType.KingBase}, Double.class, false, false) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return FLOAT_MySQL.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              date
     *                               write 需要根据数据库类型 由内置函数转换
     *
     * ****************************************************************************************************************/
    /**
     * mysql, pg
     */
    , DATE("DATE", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby}, java.sql.Date.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            Date date = DateUtil.parse(value);
            if (null == date && null != def) {
                date = DateUtil.parse(def);
            }
            if (null != date) {
                if (placeholder) {
                    value = new java.sql.Date(date.getTime());
                } else {
                    value = "'" + DateUtil.format(date, "yyyy-MM-dd");
                }
            }
            return value;
        }
    }
    /**
     * mysql(LocalDateTime)
     */
    , DATETIME("DATETIME", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.Informix}, LocalDateTime.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            Date date = DateUtil.parse(value);
            if (null == date && null != def) {
                date = DateUtil.parse(def);
            }
            if (null != date) {
                if (placeholder) {
                    value = new java.sql.Timestamp(date.getTime());
                } else {
                    value = "'" + DateUtil.format(date) + "'";
                }
            } else {
                value = null;
            }
            return value;
        }
    }
    /**
     * MSSQL
     */
    , DATETIME2("DATETIME2", new DatabaseType[]{DatabaseType.MSSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL<br/>
     * 2020-01-01 15:10:10.0000011
     */
    , DATETIMEOFFSET("DATETIMEOFFSET", new DatabaseType[]{DatabaseType.MSSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SMALLDATETIME("SMALLDATETIME", new DatabaseType[]{DatabaseType.MSSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SQL_DATETIMEOFFSET("SQL_DATETIMEOFFSET", new DatabaseType[]{DatabaseType.MSSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * MSSQL
     */
    , SECONDDATE("SECONDDATE", new DatabaseType[]{DatabaseType.HANA}, java.util.Date.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * mysql, pg
     */
    , TIME("TIME", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.HANA, DatabaseType.Derby}, java.sql.Time.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            Date date = DateUtil.parse(value);
            if (null == date && null != def) {
                date = DateUtil.parse(def);
            }
            if (null != date) {
                if (placeholder) {
                    value = new Time(date.getTime());
                } else {
                    value = "'" + DateUtil.format(date, "HH:mm:ss") + "'";
                }
            } else {
                value = null;
            }
            return value;
        }
    }
    /**
     * pg
     */
    , TIMEZ("TIMEZ", new DatabaseType[]{DatabaseType.PostgreSQL}, java.sql.Time.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return TIME.write(value, def, placeholder);
        }
    }
    /**
     * mysql, pg, oracle
     */
    , TIMESTAMP("TIMESTAMP", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.ORACLE, DatabaseType.HANA, DatabaseType.Derby}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }, TIMESTAMP_ZONE("TIMESTAMP", new DatabaseType[]{DatabaseType.PostgreSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }, TIMESTAMP_LOCAL_ZONE("TIMESTAMP", new DatabaseType[]{DatabaseType.PostgreSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * timestamp with time zone
     */
    , TIMESTAMPTZ("TIMESTAMPTZ", new DatabaseType[]{DatabaseType.PostgreSQL}, java.sql.Timestamp.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATETIME.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , YEAR("YEAR", new DatabaseType[]{DatabaseType.MySQL}, java.sql.Date.class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return DATE.write(value, def, placeholder);
        }
    }
    /* *****************************************************************************************************************
     *
     *                                              byte[]
     *
     * ****************************************************************************************************************/
    /**
     * mysql(byte[]), , oracle, sqlite
     */
    , BLOB("BLOB", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.ORACLE, DatabaseType.SQLite, DatabaseType.Informix, DatabaseType.HANA, DatabaseType.Derby, DatabaseType.KingBase}, byte[].class, true, true) {
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
     * mysql
     */
    , LONGBLOB("LONGBLOB", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , MEDIUMBLOB("MEDIUMBLOB", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , TINYBLOB("TINYBLOB", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return BLOB.write(value, def, placeholder);
        }
    }
    /**
     * mysql
     */
    , MULTILINESTRING("MULTILINESTRING", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true)
    /**
     * pg
     */
    , BYTEA("BYTEA", new DatabaseType[]{DatabaseType.PostgreSQL}, byte[].class, true, true), BYTE("BYTE", new DatabaseType[]{DatabaseType.Informix, DatabaseType.ElasticSearch}, byte[].class, true, true)
    /**
     * pg
     */
    , JSONB("JSONB", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, byte[].class, true, true) {
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
    , IMAGE("IMAGE", new DatabaseType[]{DatabaseType.MSSQL}, byte[].class, true, true)
    /**
     * oracle
     */
    , BFILE("BFILE", new DatabaseType[]{DatabaseType.ORACLE, DatabaseType.KingBase}, byte[].class, true, true)
    /* *****************************************************************************************************************
     *
     *                                              byte[]-geometry
     *
     * ****************************************************************************************************************/
    /**
     * mysql, pg
     */
    , POINT("POINT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.KingBase}, Point.class, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            if (null == value) {
                value = def;
            }
            if (value instanceof byte[]) {
                return value;
            }
            return value;
        }
    }, ST_POINT("ST_POINT", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL}, Point.class, byte[].class, true, true) {
        public Object write(Object value, Object def, boolean array, boolean placeholder) {
            return POINT.write(value, def, placeholder);
        }
    }, GEOGRAPHY_POINT("GEOGRAPHY_POINT", new DatabaseType[]{DatabaseType.VoltDB}, Point.class, byte[].class, true, true)
    /**
     * mysql
     */
    , MULTIPOLYGON("MULTIPOLYGON", new DatabaseType[]{DatabaseType.MySQL}, MultiPolygon.class, byte[].class, true, true)
    /**
     * mysql
     */
    , MULTIPOINT("MULTIPOINT", new DatabaseType[]{DatabaseType.MySQL}, MultiPoint.class, byte[].class, true, true)
    /**
     * mysql, pg
     */
    , POLYGON("POLYGON", new DatabaseType[]{DatabaseType.MySQL, DatabaseType.PostgreSQL, DatabaseType.KingBase}, Polygon.class, byte[].class, true, true)
    /**
     * mysql
     */
    , GEOMETRY("GEOMETRY", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true), ST_GEOMETRY("ST_GEOMETRY", new DatabaseType[]{DatabaseType.HANA}, byte[].class, true, true)
    /**
     * mysql
     */
    , GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", new DatabaseType[]{DatabaseType.MySQL}, byte[].class, true, true)
    /**
     * MSSQL
     */
    , HIERARCHYID("HIERARCHYID", new DatabaseType[]{DatabaseType.MSSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , LINE("LINE", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, LineString.class, byte[].class, true, true)
    /**
     * mysql
     */
    , LINESTRING("LINESTRING", new DatabaseType[]{DatabaseType.MySQL}, LineString.class, byte[].class, true, true)
    /**
     * pg
     */
    , PATH("PATH", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, LineString.class, true, true)
    /**
     * pg
     */
    , LSEG("LSEG", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, byte[].class, true, true)
    /**
     * MSSQL
     */
    , GEOGRAPHY("GEOGRAPHY", new DatabaseType[]{DatabaseType.MSSQL, DatabaseType.PostgreSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , BOX("BOX", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, byte[].class, true, true)
    /**
     * pg
     */
    , CIDR("CIDR", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, byte[].class, true, true)
    /**
     * pg
     */
    , CIRCLE("CIRCLE", new DatabaseType[]{DatabaseType.PostgreSQL}, byte[].class, true, true)
    /**
     * pg
     */
    , INET("INET", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, byte[].class, true, true)


    /* *****************************************************************************************************************
     *
     *                                              待实现
     *
     * ****************************************************************************************************************/

    /**
     * mysql
     */
    , ENUM("ENUM", new DatabaseType[]{DatabaseType.MySQL}, String.class, true, true)
    /**
     * pg
     */
    , INTERVAL("INTERVAL", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.Informix}, null, true, true)
    /**
     * oracle
     */
    , RAW("RAW", new DatabaseType[]{DatabaseType.ORACLE}, byte[].class, true, true)
    /**
     * oracle
     */
    , ROWID("ROWID", new DatabaseType[]{DatabaseType.ORACLE}, null, true, true)
    /**
     * mysql
     */
    , SET("SET", new DatabaseType[]{DatabaseType.MySQL}, String.class, true, true)
    /**
     * pg
     */
    , TSQUERY("TSQUERY", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, null, true, true)
    /**
     * pg
     */
    , TSVECTOR("TSVECTOR", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, null, true, true)
    /**
     * pg
     */
    , MACADDR("MACADDR", new DatabaseType[]{DatabaseType.PostgreSQL, DatabaseType.KingBase}, null, true, true)
    /**
     * pg
     */
    , PG_SNAPSHOT("PG_SNAPSHOT", new DatabaseType[]{DatabaseType.PostgreSQL}, null, true, true)
    /**
     * oracle
     */
    , UROWID("UROWID", new DatabaseType[]{DatabaseType.ORACLE}, null, true, true)
    /**
     * MSSQL
     */
    , SQL_VARIANT("SQL_VARIANT", new DatabaseType[]{DatabaseType.MSSQL}, null, true, true), KEYWORD("KEYWORD", new DatabaseType[]{DatabaseType.ElasticSearch}, null, true, true), OBJECT("OBJECT", new DatabaseType[]{DatabaseType.ElasticSearch}, null, true, true);
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
