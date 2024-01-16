package com.hwtx.form.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 表单变更记录
 *
 * @author hwtx
 * @since 2024-01-17
 */
@Data
@TableName("form_change_log")
@Schema(description = "表单变更记录")
public class FormChangeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "表更类型")
    private Integer type;

    @Schema(description = "变更内容")
    private String content;

    @Schema(description = "逻辑删除 1:正常  0:删除")
    private Boolean status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "最后修改时间")
    private Date lastModifyTime;

    @Schema(description = "修改者")
    private String lastModifyBy;

}

