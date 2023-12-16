package com.hwtx.form.domain.query;

import io.geekidea.boot.framework.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * App表单定义查询参数
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Data
@Schema(description = "App表单定义查询参数")
public class FormDefAppQuery extends BasePageQuery {
    private static final long serialVersionUID = 1L;

}

