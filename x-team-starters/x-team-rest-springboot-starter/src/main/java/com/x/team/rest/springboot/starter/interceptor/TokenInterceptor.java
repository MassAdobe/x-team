package com.x.team.rest.springboot.starter.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.team.common.constants.HttpConstants;
import com.x.team.common.constants.LanguageConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.RequestContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import com.x.team.log.springboot.starter.utils.XTeamLog;
import com.x.team.rest.springboot.starter.dto.Resp;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * 描述：handle with authorization
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 17:29
 */
@Slf4j
@Component
@RefreshScope
public class TokenInterceptor extends HandlerInterceptorAdapter {

    private final static AntPathMatcher MATCHER = new AntPathMatcher();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 毋需 AUTHORIZATION 的接口全名
     */
    @Value("#{'${web.exclude.urls?:none}'.split(',')}")
    private List<String> urls;

    /**
     * 预处理，在业务处理之前被调用，返回true继续执行，返回false中断执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        XTeamLog.setBegin(request.getHeader(HttpConstants.X_REQUEST_ID));
        UserContext userContext;
        // 获取当前的国际化语言
        String acceptLanguage = request.getHeader(HttpConstants.ACCEPT_LANGUAGE);
        if (StringUtils.isEmpty(acceptLanguage)) {
            // 默认中文
            acceptLanguage = LanguageConstants.ZH_CN;
        }
        userContext = UserContext.builder()
                .requestContext(RequestContext.builder()
                        .traceId(MDC.get(TraceConstants.TRACE_ID))
                        .i18n(acceptLanguage)
                        .build())
                .build();
        HeaderContext.setContext(userContext);
        log.debug("[rest-interceptor]: {}, {}", request.getContextPath(), request.getRequestURI());
        // if no use of authorization，may not auth authorization
        if (!CollectionUtils.isEmpty(this.urls) && this.urls.stream()
                .anyMatch(str -> MATCHER.match(str, request.getRequestURI()))) {
            // current request in system request without authorization
            log.debug("[rest-interceptor]: (TokenInterceptor): 当前请求: {}, 该请求毋需TOKEN验证", request.getRequestURI());
            return true;
        } else {
            String authorization = request.getHeader(HttpConstants.AUTHORIZATION);
            // when authorization is empty, return false
            if (StringUtils.isEmpty(authorization)) {
                log.error("[rest-interceptor]: (TokenInterceptor): 当前请求: {}, 当前请求不存在相关TOKEN, 定义为非法请求", request.getRequestURI());
                Resp resp = Resp.failure(ErrorCodeMsg.SYSTEM_ILLEGAL_REQUEST);
                PrintWriter writer = response.getWriter();
                writer.write(OBJECT_MAPPER.writeValueAsString(resp));
                return false;
            }
            userContext.setUserId("BDCKDIEDKELSKDEIDJNCKDLQIEJDSJEI");
            userContext.setRealName("用户名");
            HeaderContext.setContext(userContext);
        }
        return true;
    }

    /**
     * 在业务处理之后，视图生成之前被调用
     */
    @SuppressWarnings("all")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在视图生成之后被调用
     */
    @SuppressWarnings("all")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Resp.removeThreadLocal();
        super.afterCompletion(request, response, handler, ex);
    }
}
