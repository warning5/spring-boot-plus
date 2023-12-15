package io.geekidea.boot.framework.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.geekidea.boot.common.constant.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * REST API 返回结果
 *
 * @author geekidea
 * @since 2022-3-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "响应结果")
public class Api2Result implements Serializable {

    private static final long serialVersionUID = 7594052194764993562L;

    @Schema(description = "响应编码 200：成功，500：失败")
    private int code;

    @Schema(description = "响应结果 0：成功，非0：失败")
    private int status;

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "响应结果数据")
    private Object data;

    @Schema(description = "响应结果数据")
    private Map<String, String> errors;

    @Schema(description = "响应时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @Schema(description = "日志链路ID")
    private String traceId;

    public static Api2Result success() {
        return success(null);
    }

    public static <T> Api2Result success(T data) {
        return result(ApiCode.SUCCESS, data);
    }

    public static Api2Result fail() {
        return fail(ApiCode.FAIL);
    }

    public static Api2Result fail(String message) {
        return fail(ApiCode.FAIL, message);
    }

    public static Api2Result fail(ApiCode apiCode) {
        return fail(apiCode, null);
    }

    public static Api2Result fail(ApiCode apiCode, String message) {
        if (ApiCode.SUCCESS == apiCode) {
            throw new RuntimeException("失败结果状态码不能为" + ApiCode.SUCCESS.getCode());
        }
        return result(apiCode, message, null);
    }

    public static Api2Result result(boolean flag) {
        if (flag) {
            return success();
        }
        return fail();
    }

    public static Api2Result result(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static <T> Api2Result result(ApiCode apiCode, T data) {
        return result(apiCode, null, data);
    }

    public static String build(ApiCode apiCode, String message, String data) {
        return "{" +
                "\"code\":" + apiCode.getCode() + "," +
                "\"status\":" + (ApiCode.SUCCESS.getCode() == apiCode.getCode() ? 0 : -1) + "," +
                "\"msg\":" + "\"" + message + "\"," +
                "\"data\":" + data +
                "}";
    }

    public static <T> Api2Result validate(ApiCode apiCode, String message, Map<String, String> errors) {
        return buildResult(apiCode, message, null, errors);
    }

    public static <T> Api2Result result(ApiCode apiCode, String message, T data) {

        return buildResult(apiCode, message, data, null);
    }

    private static <T> Api2Result buildResult(ApiCode apiCode, String message, T data, Map<String, String> errors) {
        if (apiCode == null) {
            throw new RuntimeException("结果状态码不能为空");
        }
        boolean success = false;
        int code = apiCode.getCode();
        if (ApiCode.SUCCESS.getCode() == code) {
            success = true;
        }
        String outMessage;
        if (StringUtils.isBlank(message)) {
            outMessage = apiCode.getMsg();
        } else {
            outMessage = message;
        }
        String traceId = MDC.get(CommonConstant.TRACE_ID);
        Api2Result api2Result = new Api2Result();
        api2Result.setCode(code);
        api2Result.setMsg(outMessage);
        api2Result.setData(data);
        api2Result.setStatus(success ? 0 : -1);
        api2Result.setTime(new Date());
        api2Result.setTraceId(traceId);
        if (errors != null) {
            api2Result.setErrors(errors);
        }
        return api2Result;
    }
}
