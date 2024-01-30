package io.geekidea.boot.generator.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author geekidea
 * @date 2023/12/31
 **/
@Slf4j
public class VelocityUtil {

    private static Properties properties;

    static {
        properties = new Properties();
        properties.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
        properties.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
        properties.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
        properties.setProperty("file.resource.loader.unicode", StringPool.TRUE);
    }

    /**
     * 渲染模板数据输出到StringWrite中
     *
     * @param templatePath
     * @param dataMap
     * @return
     * @throws Exception
     */
    public static String writer(String templatePath, Map<String, Object> dataMap) {
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        StringWriter writer = new StringWriter();
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        template.merge(new VelocityContext(dataMap), writer);
        String content = writer.toString();
        IOUtils.closeQuietly(writer);
        return content;
    }


}
