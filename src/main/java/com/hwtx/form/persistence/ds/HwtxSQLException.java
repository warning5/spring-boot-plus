
package com.hwtx.form.persistence.ds;

import java.util.List;

public class HwtxSQLException extends RuntimeException{
	protected Exception src;
	protected String sql;
	protected List<Object> values;
	public HwtxSQLException(){
		super(); 
	}
	public HwtxSQLException(String title){
		super(title);
	}
	public HwtxSQLException(String title, Exception src){
		super(title, src);
		if(null != src) {
			super.setStackTrace(src.getStackTrace());
		}
	}

	public Exception getSrc() {
		return src;
	}

	public void setSrc(Exception src) {
		if(null != src) {
			super.setStackTrace(src.getStackTrace());
		}
		this.src = src;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
}
