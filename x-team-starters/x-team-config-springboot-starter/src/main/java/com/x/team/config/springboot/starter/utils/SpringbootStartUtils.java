package com.x.team.config.springboot.starter.utils;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.LogConstants;

/**
 * 描述：springboot launch utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:48
 */
public class SpringbootStartUtils {

    /**
     * setting global param when launch (logs)
     */
    public static void settingLog() {
        System.setProperty(LogConstants.NACOS_LOG_SETTING, CommonConstants.FALSE_STRING_LABEL);
    }
}
