package com.x.team.config.springboot.starter.web;

import com.x.team.common.constants.HttpConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：warm up
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 15:03
 */
@RestController
public class WarmUpController {

    private final static String DEFAULT_RETURN_SENTENCE = "{\"status\": \"0\",\"msg\":\"SUCCESS\",\"data\":\"healthy\"}";

    @RequestMapping(value = "/health", method = RequestMethod.GET, produces = {HttpConstants.APPLICATION_JSON_UTF8})
    public String health() {
        return DEFAULT_RETURN_SENTENCE;
    }
}
