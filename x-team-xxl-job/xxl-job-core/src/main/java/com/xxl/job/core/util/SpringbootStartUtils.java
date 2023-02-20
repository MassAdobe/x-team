package com.xxl.job.core.util;

/**
 * 描述：springboot launch utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:48
 */
public class SpringbootStartUtils {

    /**
     * NACOS LOGGING PATH
     */
    private final static String NACOS_LOG_SETTING = "nacos.logging.default.config.enabled";

    /**
     * FALSE LABEL
     */
    private final static String FALSE_STRING_LABEL = "false";

    /**
     * setting global param when launch (logs)
     */
    public static void settingLog() {
        System.setProperty(NACOS_LOG_SETTING, FALSE_STRING_LABEL);
    }
}
