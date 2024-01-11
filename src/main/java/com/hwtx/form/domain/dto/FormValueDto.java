package com.hwtx.form.domain.dto;

import io.geekidea.boot.common.constant.SystemConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 修改表单值参数
 *
 * @author hwtx
 * @since 2023-12-16
 */
@Data
@Schema(description = "修改表单值参数")
public class FormValueDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "表单名称")
    @NotBlank(message = "表单名称不能为空")
    @Length(max = 200, message = "表单名称长度超过限制")
    private String form;

    @Schema(description = "页面编号")
    @NotBlank(message = "页面编号不能为空")
    @Length(max = 100, message = "页面编号长度超过限制")
    private String page;

    @Schema(description = "表单值")
    @NotBlank(message = "表单值不能为空")
    @Length(max = 65535, message = "表单值长度超过限制")
    private Map<String, Object> formData;

    @Schema(description = "表单项值的归属主体1")
    @NotBlank(message = "表单项值的归属主体1不能为空")
    @Length(max = 100, message = "表单项值的归属主体1长度超过限制")
    private String k1;

    @Schema(description = "表单项值的归属主体2")
    @NotBlank(message = "表单项值的归属主体2不能为空")
    @Length(max = 100, message = "表单项值的归属主体2长度超过限制")
    private String k2;

    @Schema(description = "表单项值的归属主体3")
    @NotBlank(message = "表单项值的归属主体3不能为空")
    @Length(max = 100, message = "表单项值的归属主体3长度超过限制")
    private String k3;

    @Schema(description = "逻辑删除 1:正常  0:删除")
    private Boolean status;

    public void setCreateBy(String createBy) {
        formData.put(SystemConstant.create_by, createBy);
    }

    public void setCreateTime(Date createTime) {
        formData.put(SystemConstant.create_time, createTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        formData.put(SystemConstant.last_modify_time, lastModifyTime);
    }

    public void setLastModifyBy(String lastModifyBy) {
        formData.put(SystemConstant.last_modify_by, lastModifyBy);
    }

    public String getCreateBy() {
        return (String) formData.get(SystemConstant.create_by);
    }

    public Date getCreateTime() {
        return (Date) formData.get(SystemConstant.create_time);
    }

    public Date getLastModifyTime() {
        return (Date) formData.get(SystemConstant.last_modify_time);
    }

    public String getLastModifyBy() {
        return (String) formData.get(SystemConstant.last_modify_by);
    }
}


