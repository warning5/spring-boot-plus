package com.hwtx.form.controller;

import com.hwtx.form.domain.FormServiceImpl;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.service.FormService;
import io.geekidea.boot.framework.response.Api2Result;
import io.geekidea.boot.framework.response.ApiCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "表单管理")
@RequestMapping("/form")
public class FormController {

    @Resource
    private FormService formService;

    @GetMapping("/handle/")
    @Operation(summary = "处理表单变化")
    public Api2Result gen(@RequestBody FormHandleParam formHandleParam) throws Exception {
        if (formHandleParam.getFormId() == null) {
            return Api2Result.fail(ApiCode.FAIL, "表单ID不能为空");
        }
        if (formHandleParam.getHandleAction() == null) {
            return Api2Result.fail(ApiCode.FAIL, "表单处理动作不可为空");
        }
        formService.handleForm(formHandleParam);
        return Api2Result.result(ApiCode.SUCCESS, "处理成功", "");
    }

    @GetMapping("/load/{formId}")
    @Operation(summary = "获取表单定义")
    public String load(@PathVariable Long formId) throws Exception {
        String formContent = formService.getRawFormDef(formId);
        return Api2Result.build(ApiCode.SUCCESS, "加载成功", formContent);
    }

    @PostMapping("/save")
    @Operation(summary = "获取表单定义")
    public Api2Result save(@RequestBody Map<String, String> content) throws Exception {
        String formId = content.get(FormServiceImpl.INPUT_FORM_ID);
        if (StringUtils.isEmpty(formId)) {
            return Api2Result.fail(ApiCode.FAIL, "表单ID不能为空");
        }
        Map<String, String> validationResult = formService.validateForm(Long.parseLong(formId), content);
        if (!validationResult.isEmpty()) {
            return Api2Result.validate(ApiCode.FAIL, "参数校验失败", validationResult);
        }
        formService.saveFormData(content, "admin");
        return Api2Result.result(ApiCode.SUCCESS, "保存成功", "");
    }

    @GetMapping("/get")
    @Operation(summary = "获取表单数据")
    public String get(FormValueQuery formValueQuery) throws Exception {
        if (formValueQuery.getValueId() == null) {
            return Api2Result.build(ApiCode.SUCCESS, "", "{}");
        }
        formValueQuery.setUser("admin");
        return Api2Result.build(ApiCode.SUCCESS, "加载成功", formService.getFormData(formValueQuery));
    }

    @PostMapping("/remove")
    @Operation(summary = "删除表单数据")
    public String remove(FormValueQuery formValueQuery) throws Exception {
        if (formValueQuery.getValueId() == null) {
            return Api2Result.build(ApiCode.FAIL, "数据ID不能为空", "");
        }
        formValueQuery.setUser("admin");
        formService.removeValue(formValueQuery);
        return Api2Result.build(ApiCode.SUCCESS, "删除成功", "");
    }
}
