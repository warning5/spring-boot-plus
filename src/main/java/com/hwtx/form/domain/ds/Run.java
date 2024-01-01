package com.hwtx.form.domain.ds;

import org.anyline.entity.Compare;
import org.anyline.entity.Compare.EMPTY_VALUE_SWITCH;
import org.anyline.entity.Order;
import org.anyline.entity.OrderStore;
import org.anyline.entity.PageNavi;
import org.anyline.metadata.ACTION;
import org.anyline.metadata.Column;

import java.util.LinkedHashMap;
import java.util.List;

public interface Run {
	Run setRuntime(DataRuntime runtime);
	void init();
	DriverAdapter adapter();
	/** 
	 * 添加查询条件 
	 * @param swt 				遇到空值处理方式
	 * @param prefix  			查询条件ID或表名
	 * @param variable  		列名|变量key
	 * @param value  			值
	 * @param compare 			比较方式
	 * @return Run 最终执行命令 如果是JDBC类型库 会包含 SQL 与 参数值
	 */
	Run setConditionValue(EMPTY_VALUE_SWITCH swt, Compare compare, String prefix, String variable, Object value);
	Run group(String group);
 
	void setOrderStore(OrderStore orders) ; 
	void setOrders(String[] orders); 
	OrderStore getOrderStore() ; 
	Run order(String order);
	/**
	 * 添加参数值
	 * @param compare  compare
	 * @param column  column
	 * @param obj  obj
	 * @param split 遇到集合/数组类型是否拆分处理
	 * @return Run 最终执行命令 如果是JDBC类型库 会包含 SQL 与 参数值
	 */
	RunValue addValues(Compare compare, Column column, Object obj, boolean split);
	Run addValue(RunValue value);
	Run addOrders(OrderStore orderStore);
	Run addOrder(Order order);
	String getTable();
	String getCatalog();
	String getSchema();
	String getDataSource();
	Run setInsertColumns(List<String> keys);
	Run setInsertColumns(LinkedHashMap<String, Column> columns);
	List<String> getInsertColumns();
	LinkedHashMap<String, Column> getInsertColumns(boolean metadata);
	Run setUpdateColumns(List<String> keys);
	Run setUpdateColumns(LinkedHashMap<String, Column> columns);
	List<String> getUpdateColumns();
	LinkedHashMap<String, Column> getUpdateColumns(boolean metadata);
	String getBaseQuery(boolean placeholder) ;
	default String getBaseQuery() {
		return getBaseQuery(true);
	}
	String getFinalExists(boolean placeholder);
	default String getFinalExists(){
		return getFinalExists(true);
	}
	String getFinalInsert(boolean placeholder);
	default String getFinalInsert(){
		return getFinalInsert(true);
	}
	String getFinalDelete(boolean placeholder);
	default String getFinalDelete(){
		return getFinalDelete(true);
	}
	String getFinalUpdate(boolean placeholder);
	default String getFinalUpdate(){
		return getFinalUpdate(true);
	}
	String getFinalExecute(boolean placeholder);
	default String getFinalExecute(){
		return getFinalExecute(true);
	}

	/**
	 * SQL是否支持换行
	 * @return boolean
	 */
	default boolean supportBr(){
		return true;
	}
	void supportBr(boolean support);

	List<RunValue> getRunValues() ;
	List<Object> getValues() ;
	PageNavi getPageNavi() ; 
	void setPageNavi(PageNavi pageNavi) ;
	EMPTY_VALUE_SWITCH getStrict();

	void setSwitch(EMPTY_VALUE_SWITCH swt);
	boolean isValid();
	boolean checkValid();
	void setValid(boolean valid);
	StringBuilder getBuilder();
	void setBuilder(StringBuilder builder);
	//1-DataRow 2-Entity
	int getFrom();
	void setFrom(int from);
	void setFilter(Object filter);
	Object getFilter();
	void setUpdate(Object update);
	Object getUpdate();
	Run setQueryColumns(String ... columns);
	Run setQueryColumns(List<String> columns);
	List<String> getQueryColumns();

	List<String> getExcludeColumns();
	Run setExcludeColumns(List<String> excludeColumn);
	Run setExcludeColumns(String... columns);

	void setValue(Object value);
	void setValues(String key, List<Object> values);
	Object getValue();
	void setBatch(int batch);
	int getBatch();
	void setVol(int vol);
	int getVol();
	String action();
	void action(String action);

	String log(ACTION.DML action, boolean placeholder);

}
