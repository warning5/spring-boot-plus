package com.hwtx.framework.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 表单值VO
 *
 * @author hwtx
 * @since 2023-12-16
 */
@Data
@Schema(description = "表单值查询结果")
public class FormValueVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "表单名称")
    private String form;

    @Schema(description = "页面编号")
    private String page;

    @Schema(description = "表单值")
    private String content;

    @Schema(description = "表单项值的归属主体1")
    private String k1;

    @Schema(description = "表单项值的归属主体2")
    private String k2;

    @Schema(description = "表单项值的归属主体3")
    private String k3;

    @Schema(description = "逻辑删除 1:正常  0:删除")
    private Boolean status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "最后修改时间")
    private Date lastModifyTime;

    private String lastModifyBy;

}

