package com.xxl.job.admin.controller;

import com.x.team.common.constants.HttpConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：warm up
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 17:18
 */
@RestController
public class WarmUpController {

    private final static String DEFAULT_RETURN_SENTENCE = "{\"code\":200,\"msg\":\"SUCCESS\",\"data\":\"okay\"}";

    @RequestMapping(value = "/warmup", method = RequestMethod.GET, produces = {HttpConstants.APPLICATION_JSON_UTF8})
    public String warmup() {
        return DEFAULT_RETURN_SENTENCE;
    }
}
