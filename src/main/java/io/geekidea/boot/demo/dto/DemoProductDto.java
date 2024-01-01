package io.geekidea.boot.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改测试商品参数
 *
 * @author geekidea
 * @since 2023-12-05
 */
@Data
@Schema(description = "修改测试商品参数")
public class DemoProductDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商品名称")
    @NotBlank(message = "商品名称不能为空")
    @Length(max = 20, message = "商品名称长度超过限制")
    private String name;

    @Schema(description = "商户ID")
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @Schema(description = "备注")
    @Length(max = 200, message = "备注长度超过限制")
    private String remark;

    @Schema(description = "状态，0：禁用，1：启用")
    private Boolean status;

}


