package com.hwtx.form.controller;

import com.hwtx.form.domain.FormListService;
import com.hwtx.form.domain.FormServiceImpl;
import com.hwtx.form.domain.dto.FormListQuery;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.service.FormService;
import io.geekidea.boot.framework.response.Api2Result;
import io.geekidea.boot.framework.response.ApiCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.hwtx.form.domain.FormConstants.INPUT_FORM_ID;

@Slf4j
@RestController
@Tag(name = "表单管理")
@RequestMapping("/form")
public class FormController {

    @Resource
    private FormService formService;
    @Resource
    private FormListService formListService;

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
        String formId = content.get(INPUT_FORM_ID);
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
    public Api2Result get(FormValueQuery formValueQuery) throws Exception {
        if (formValueQuery.getValueId() == null) {
            return Api2Result.result(ApiCode.SUCCESS, "", "{}");
        }
        formValueQuery.setUser("admin");
        return Api2Result.result(ApiCode.SUCCESS, "加载成功", formService.getFormData(formValueQuery).getData());
    }

    @PostMapping("/remove")
    @Operation(summary = "删除表单数据")
    public Api2Result remove(FormValueQuery formValueQuery) throws Exception {
        if (formValueQuery.getValueId() == null) {
            return Api2Result.result(ApiCode.FAIL, "数据ID不能为空", "");
        }
        formValueQuery.setUser("admin");
        formService.removeValue(formValueQuery);
        return Api2Result.result(ApiCode.SUCCESS, "删除成功", "");
    }

    @GetMapping("/list")
    @Operation(summary = "获取表单列表")
    public Api2Result pageList(FormListQuery formListQuery, @PageableDefault(page = 1, size = 20) Pageable pageable,
                               HttpServletRequest request) throws Exception {
        if (formListQuery.getFormId() == null) {
            return Api2Result.result(ApiCode.FAIL, "表单ID不能为空", "");
        }
        formListQuery.setSearchData(request.getParameterMap());
        return Api2Result.result(ApiCode.SUCCESS, "查询成功", formListService.list(formListQuery, "admin", pageable));
    }
}
