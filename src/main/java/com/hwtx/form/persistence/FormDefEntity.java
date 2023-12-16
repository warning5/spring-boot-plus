package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 表单定义
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Data
@TableName("form_def")
@Schema(description = "表单定义")
public class FormDefEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "表单内容")
    private String content;

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

