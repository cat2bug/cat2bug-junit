package com.cat2bug.junit.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 配置类
 */
public class ConfigUtil {

    private static final String SPRINGBOOT_YAML_FILE = "application.yml"; // YAML 配置文件路径
    private static final String CAT2BUG_YAML_FILE = "cat2bug.yml"; // YAML 配置文件路径

    private static final String SPRINGBOOT_PROPERTIES_FILE = "application.properties"; // properties 配置文件路径
    private static final String CAT2BUG_PROPERTIES_FILE = "cat2bug.properties"; // properties 配置文件路径

    /**
     * 获取配置
     * @param key   配置名
     * @param cls   配置类
     * @return      配置数据
     * @param <T>   配置数据类型
     */
    public static <T> T getConfig(String key,Class<T> cls) {
        Map<String, Object> map = loadConfig();
        Object ret = map.get(key);
        if(ret==null) {
            return null;
        }
        return cls.cast(ret);
    }

    private static Map<String, Object> loadConfig() {
        InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(CAT2BUG_YAML_FILE);
        if (inputStream != null) {
            // 读取 YAML 文件
            return loadYamlConfig(inputStream);
        } else {
            // 读取 properties 文件
            return loadPropertiesConfig();
        }
    }

    private static Map<String, Object> loadYamlConfig(InputStream inputStream) {
        Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }

    private static Map<String, Object> loadPropertiesConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(CAT2BUG_PROPERTIES_FILE)) {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Map) properties;
    }
}
