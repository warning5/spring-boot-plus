package com.hwtx.form.domain.query;

import io.geekidea.boot.framework.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 表单定义查询参数
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Data
@Schema(description = "表单定义查询参数")
public class FormValueQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long valueId;
    private String formId;
    private String pageX;
    private String user;
}

