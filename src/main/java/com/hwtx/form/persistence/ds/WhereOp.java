package com.hwtx.form.persistence.ds;

public enum WhereOp {
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL");
    private final String op;

    WhereOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return this.op;
    }
}
