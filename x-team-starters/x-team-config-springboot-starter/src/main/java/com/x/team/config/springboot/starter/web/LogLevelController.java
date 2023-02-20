package com.x.team.config.springboot.starter.web;

import com.x.team.common.constants.HttpConstants;
import com.x.team.log.springboot.starter.service.LogLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：日志级别
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 15:02
 */
@RestController
public class LogLevelController {

    private final static String DEFAULT_RETURN_SENTENCE = "{\"status\":\"0\",\"msg\":\"SUCCESS\",\"data\":\"%s\"}";

    private final static List<String> DEFAULT_LOG_LEVEL_LIST = new ArrayList<>();

    private final static String ERROR_LOG_LEVEL_INFO = "当前日志级别给定不正确，无法进行动态调整";

    static {
        DEFAULT_LOG_LEVEL_LIST.add("trace");
        DEFAULT_LOG_LEVEL_LIST.add("debug");
        DEFAULT_LOG_LEVEL_LIST.add("info");
        DEFAULT_LOG_LEVEL_LIST.add("warm");
        DEFAULT_LOG_LEVEL_LIST.add("error");
        DEFAULT_LOG_LEVEL_LIST.add("off");
        DEFAULT_LOG_LEVEL_LIST.add("on");
    }

    @Autowired
    private LogLevelService logLevelService;

    @RequestMapping(value = "/logLevel", method = RequestMethod.PUT, produces = {HttpConstants.APPLICATION_JSON_UTF8})
    public String logLevel(@RequestParam("level") String level) {
        if (DEFAULT_LOG_LEVEL_LIST.contains(level)) {
            return String.format(DEFAULT_RETURN_SENTENCE, this.logLevelService.resetLogLevel(level));
        } else {
            return String.format(DEFAULT_RETURN_SENTENCE, ERROR_LOG_LEVEL_INFO);
        }
    }
}
