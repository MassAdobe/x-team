package com.x.team.rest.springboot.starter.aop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.team.common.constants.CommonConstants;
import com.x.team.rest.springboot.starter.dto.Resp;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 描述：aspect of logging
 *
 * @author MassAdobe
 * @date Created in 2023/1/29 11:15
 */
@Slf4j
@Aspect
@Component
public class HttpAspect {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private final static String WARM_UP_MARK = "/**/warmup";

    private final static AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * every controller
     */
    @Pointcut("execution(public * com.x.team.*.rest.controller.*.*(..))")
    public void controllerMethod() {
    }

    /**
     * 描述：execute before function executing
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 11:16
     */
    @Before("controllerMethod()")
    public void logBefore(JoinPoint joinPoint) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        // WARM UP API NO PRINT LOG
        if (MATCHER.match(WARM_UP_MARK, request.getRequestURI())) {
            return;
        }
        StringBuilder requestLog = new StringBuilder();
        Signature signature = joinPoint.getSignature();
        requestLog.append("request info：").append("URL: {").append(request.getRequestURI()).append("},")
                .append("request function: {").append(request.getMethod()).append("},")
                .append("request ip: {").append(request.getRemoteAddr()).append("},")
                .append("bean function: {").append(signature.getDeclaringTypeName()).append(CommonConstants.POT_MARK)
                .append(signature.getName()).append("},");
        // 处理请求参数
        String[] paramNames = ((MethodSignature) signature).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        int paramLength = null == paramNames ? 0 : paramNames.length;
        if (paramLength == 0) {
            requestLog.append("request params: {} ");
        } else {
            requestLog.append("request params: [");
            for (int i = 0; i < paramLength - 1; i++) {
                if (paramValues[i] instanceof HttpServletRequest || paramValues[i] instanceof HttpServletResponse || paramValues[i] instanceof MultipartFile) {
                    continue;
                }
                requestLog.append(paramNames[i]).append(": ").append(OBJECT_MAPPER.writeValueAsString(paramValues[i])).append(",");
            }
            if (paramValues[paramLength - 1] instanceof HttpServletRequest || paramValues[paramLength - 1] instanceof HttpServletResponse || paramValues[paramLength - 1] instanceof MultipartFile) {
                requestLog.append("]");
            } else {
                requestLog.append(paramNames[paramLength - 1]).append(": ").append(OBJECT_MAPPER.writeValueAsString(paramValues[paramLength - 1])).append("]");
            }
        }
        log.info("[rest-aop]: request-log: {}", requestLog);
    }


    /**
     * 描述：execute after function executing
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 11:16
     */
    @SuppressWarnings("all")
    @AfterReturning(returning = "resp", pointcut = "controllerMethod()")
    public void logAfter(Resp resp) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        // WARM UP API NO PRINT LOG
        if (MATCHER.match(WARM_UP_MARK, request.getRequestURI())) {
            return;
        }
        log.info("[rest-aop]: response-log: {}, params: {}", request.getRemoteAddr(), resp.toString().replaceAll(REMOVE_RETURN_NO_USE, CommonConstants.EMPTY));
    }

    /**
     * 去除无用信息
     */
    private final static String REMOVE_RETURN_NO_USE = ", clazz=null, resp=null";

    /**
     * 描述：function throw errors
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 11:17
     */
    @AfterThrowing(throwing = "throwable", pointcut = "controllerMethod()")
    public void logThrow(Throwable throwable) {
        log.error("[rest-aop]: has intercept exception: {}", throwable.getMessage());
    }

    /**
     * 描述：包裹打印请求耗时
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 11:30
     */
    @Around(value = "controllerMethod()")
    private Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 步入时间戳
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object proceed = proceedingJoinPoint.proceed();
        // WARM UP API NO PRINT LOG
        if (MATCHER.match(WARM_UP_MARK, request.getRequestURI())) {
            return proceed;
        }
        log.info("[rest-aop]: request-response-time: {}ms", System.currentTimeMillis() - startTime);
        return proceed;
    }

}
