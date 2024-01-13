package com.hwtx.form.persistence.ds;

public class HwtxSQLUpdateException extends HwtxSQLException {
    private static final long serialVersionUID = 1L;

    public HwtxSQLUpdateException() {
        super();
    }

    public HwtxSQLUpdateException(String title) {
        super(title);
    }

    public HwtxSQLUpdateException(String title, Exception src) {
        super(title, src);
    }
} 
