package com.x.team.log.springboot.starter.utils;

import com.x.team.common.constants.SqlConstants;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：log utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:56
 */
public class XTeamLog {

    private final static String JSON_REG = "\"id\":(\\d*)[,]*";

    /**
     * setting trace id in the most beginning
     * <p/>
     * first param is trace_id
     * <p/>
     * second param is step_id
     */
    @SuppressWarnings("all")
    public static void setBegin(String... traceId) {
        if (null != traceId && 0 != traceId.length) {
            MDC.put(TraceConstants.TRACE_ID, traceId[0]);
            if (2 == traceId.length) {
                MDC.put(TraceConstants.STEP_ID, traceId[1]);
            } else {
                MDC.put(TraceConstants.STEP_ID, SqlConstants.UNDELETED);
            }
        } else {
            if (StringUtils.isEmpty(MDC.get(TraceConstants.TRACE_ID))) {
                MDC.put(TraceConstants.TRACE_ID, TraceUtils.generateTraceId());
            }
            if (StringUtils.isEmpty(MDC.get(TraceConstants.STEP_ID))) {
                MDC.put(TraceConstants.STEP_ID, SqlConstants.UNDELETED);
            }
        }
    }

    /**
     * exist trace
     */
    public static boolean existTrace() {
        return StringUtils.isEmpty(MDC.get(TraceConstants.TRACE_ID));
    }

    /**
     * end live of trace id
     */
    public static void removeBegin() {
        MDC.clear();
    }

    /**
     * get id from json
     */
    @SuppressWarnings("all")
    public static String findIdFromJson(String payload) {
        Matcher matcher = Pattern.compile(JSON_REG).matcher(payload.replaceAll("\\s", ""));
        if (matcher.find()) {
            return matcher.group().split("\\:")[1].replaceAll(",", "");
        }
        return null;
    }
}
