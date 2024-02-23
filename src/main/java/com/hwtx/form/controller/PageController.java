package com.hwtx.form.controller;

import io.geekidea.boot.framework.response.ApiCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "页面管理")
@RequestMapping("/page")
public class PageController {

    @GetMapping("/load/{pageId}")
    @Operation(summary = "获取页面定义")
    public String loadPage(@PathVariable String pageId) throws Exception {
        if (StringUtils.isEmpty(pageId)) {
            return Api2Result.build(ApiCode.FAIL, "页面ID不能为空", "");
        }
        String page = Objects.requireNonNull(PageController.class.getClassLoader().getResource("page/" + pageId + ".json")).getFile();
        return Api2Result.build(ApiCode.SUCCESS, "加载成功", FileUtils.readFileToString(new File(page), StandardCharsets.UTF_8));
    }
}
