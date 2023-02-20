package com.x.team.common.context;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.LanguageConstants;
import com.x.team.common.constants.SqlConstants;
import com.x.team.common.dto.grpc.header.RequestContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.enums.RequestType;
import com.x.team.common.utils.CommonUtils;

import java.util.Objects;

/**
 * 描述：头信息
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 19:13
 */
@SuppressWarnings("all")
public class HeaderContext {

    private final static ThreadLocal<UserContext> THREAD_LOCAL = new ThreadLocal();

    public static void setContext(UserContext userContext) {
        if (Objects.nonNull(THREAD_LOCAL.get())) {
            clearContext();
        }
        THREAD_LOCAL.set(userContext);
    }

    /**
     * 设置例如线程池的无头调用头
     */
    public static void setNoHeaderContext() {
        THREAD_LOCAL.set(UserContext.builder()
                .requestContext(RequestContext.builder()
                        .requestType(RequestType.NO_HEADER)
                        .i18n(LanguageConstants.EN_US)
                        .stepId(SqlConstants.UNDELETED)
                        .traceId(CommonUtils.getRandomString(18)
                                + CommonConstants.HORIZONTAL_MARK
                                + System.currentTimeMillis())
                        .build())
                .build());
    }

    public static UserContext getContext() {
        return THREAD_LOCAL.get();
    }

    public static void clearContext() {
        THREAD_LOCAL.remove();
    }

    public static boolean existContext() {
        return Objects.nonNull(THREAD_LOCAL.get());
    }
}
