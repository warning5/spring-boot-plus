package com.hwtx.form;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwtx.form.domain.def.FormDef;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File(Main.class.getClassLoader().getResource("1.json").getFile());
        FormDef formDef = JSON.parseObject(FileUtils.readFileToString(file, "utf-8"), FormDef.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(formDef));

    }
}
