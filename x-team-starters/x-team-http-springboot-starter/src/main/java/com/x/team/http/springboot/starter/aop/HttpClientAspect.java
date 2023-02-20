package com.x.team.http.springboot.starter.aop;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.utils.CommonUtils;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：http-client aspect
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:29
 */
@Slf4j
@Aspect
@Component
public class HttpClientAspect {

    /**
     * 所有controller
     */
    @Pointcut("execution(public * com.x.team.http.springboot.starter.client.HttpClient.*(..))")
    public void httpMethod() {
    }

    /**
     * 描述：around function when execting
     *
     * @author MassAdobe
     * @date Created in 2023/1/20 16:29
     */
    @Around("httpMethod()")
    @SuppressWarnings("all")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // first get a list of method names
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();
        List<String> paramNameList = Arrays.asList(paramNames);
        // get the incoming parameters
        Object[] args = proceedingJoinPoint.getArgs();
        // 如果有headers这个参数
        if (paramNameList.contains("headers")) {
            // If there is a parameter of headers
            int headers = paramNameList.indexOf("headers");
            // return params
            HashMap<String, String> paramValue = (HashMap<String, String>) args[headers];
            if (Objects.isNull(paramValue) || paramValue.isEmpty()) {
                Map<String, String> newParamValue = new HashMap<>(4);
                if (StringUtils.isEmpty(MDC.get(TraceConstants.TRACE_ID))) {
                    newParamValue.put(TraceConstants.TRACE_ID, CommonUtils.getRandomString(10) + CommonConstants.HORIZONTAL_MARK + System.currentTimeMillis());
                } else {
                    newParamValue.put(TraceConstants.TRACE_ID, MDC.get(TraceConstants.TRACE_ID));
                }
                args[headers] = newParamValue;
            } else {
                if (StringUtils.isEmpty(MDC.get(TraceConstants.TRACE_ID))) {
                    paramValue.put(TraceConstants.TRACE_ID, CommonUtils.getRandomString(10) + CommonConstants.HORIZONTAL_MARK + System.currentTimeMillis());
                } else {
                    paramValue.put(TraceConstants.TRACE_ID, MDC.get(TraceConstants.TRACE_ID));
                }
                args[headers] = paramValue;
            }
        }
        // modify parameter value
        return proceedingJoinPoint.proceed(args);
    }
}
