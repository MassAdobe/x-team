package com.x.team.schedule.springboot.starter.aop;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.LanguageConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.RequestContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.enums.RequestType;
import com.x.team.common.utils.CommonUtils;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import com.x.team.log.springboot.starter.utils.XTeamLog;
import com.x.team.schedule.springboot.starter.constants.ScheduleConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 描述：定时任务AOP
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 16:27
 */
@Aspect
@Component
public class ScheduleAspect {

    @Pointcut("@annotation(com.x.team.schedule.springboot.starter.annotation.Schedule)")
    public void scheduleCut() {

    }

    /**
     * 包裹
     */
    @Around("scheduleCut()")
    public Object doSchedule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 设置头信息
        RequestContext requestContext = RequestContext.builder()
                .traceId(ScheduleConstants.SCHEDULE_LOG_PREFIX
                        + CommonUtils.getRandomString(8)
                        + CommonConstants.HORIZONTAL_MARK
                        + System.currentTimeMillis())
                .stepId(TraceConstants.FIRST_STEP)
                .i18n(LanguageConstants.EN_US)
                .requestType(RequestType.SCHEDULE_CALL)
                .build();
        HeaderContext.setContext(UserContext.builder()
                .requestContext(requestContext)
                .build());
        XTeamLog.setBegin(requestContext.getTraceId(), requestContext.getStepId());
        // 业务
        Object[] args = proceedingJoinPoint.getArgs();
        Object proceed = proceedingJoinPoint.proceed(args);
        // 清除头信息
        HeaderContext.clearContext();
        XTeamLog.removeBegin();
        return proceed;
    }
}
