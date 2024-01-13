package com.hwtx.form.persistence.ds;

import lombok.Getter;

@Getter
public enum DefaultColumn {
    create_time("创建时间"),
    create_by("创建人"),
    last_modify_time("最后修改时间"),
    last_modify_by("修改人"),
    status("逻辑删除 1:正常  0:删除"),
    id("主键");

    public static final int Status_NORMAL = 1;
    public static final int Status_DELETED = 0;

    final String comment;

    DefaultColumn(String comment) {
        this.comment = comment;
    }
}
