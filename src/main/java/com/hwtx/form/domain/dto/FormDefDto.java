package com.hwtx.form.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改表单定义参数
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Data
@Schema(description = "修改表单定义参数")
public class FormDefDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "表单名称")
    @Length(max = 200, message = "表单名称长度超过限制")
    private String name;

    @Schema(description = "表单内容")
    @Length(max = 65535, message = "表单内容长度超过限制")
    private String content;

    @Schema(description = "逻辑删除 1:正常  0:删除")
    private Boolean status;

    @Schema(description = "创建者")
    @Length(max = 64, message = "创建者长度超过限制")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最后修改时间")
    private Date lastModifyTime;

    @Length(max = 64, message = "长度超过限制")
    private String lastModifyBy;

}


