package com.hwtx.form.domain.ds.metadata;

public interface DataType {
    public static DataType ILLEGAL = new DataType() {

        @Override
        public Object read(Object value, Object def, Class clazz) {
            return null;
        }

        @Override
        public Object write(Object value, Object def, boolean placeholder) {
            return null;
        }

        @Override
        public boolean ignorePrecision() {
            return false;
        }

        @Override
        public boolean ignoreScale() {
            return false;
        }

        @Override
        public boolean support() {
            return false;
        }

        @Override
        public Class supportClass() {
            return null;
        }
    };

    /**
     * 从数据库中读取数据, 常用的基本类型可以自动转换, 不常用的如json/point/polygon/blob等转换成anyline对应的类型
     *
     * @param value value
     * @param def   默认值
     * @param clazz 目标数据类型(给entity赋值时可以根据class, DataRow赋值时可以指定class，否则按检测metadata类型转换 转换不不了的原样返回)
     * @return Object
     */
    public abstract Object read(Object value, Object def, Class clazz);

    /**
     * 写入数据库前类型转换<br/>
     * 如果有占位符成数据库可接受的Java数据类型<br/>
     * 如果没有占位符 需要确定加单引号或内置函数<br/>
     *
     * @param placeholder 是否占位符
     * @param value       value
     * @param def         默认值
     * @return Object
     */
    Object write(Object value, Object def, boolean placeholder);

    /**
     * 定义列时 数据类型格式
     *
     * @return boolean
     */
    boolean ignorePrecision();

    boolean ignoreScale();

    boolean support();

    Class supportClass();
}
