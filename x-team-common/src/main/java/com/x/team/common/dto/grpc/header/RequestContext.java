package com.x.team.common.dto.grpc.header;

import com.x.team.common.constants.CommonConstants;
import com.x.team.common.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：请求信息
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 19:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {
    /**
     * TraceId
     */
    private String traceId;
    /**
     * StepId
     */
    private String stepId;
    /**
     * 国际化
     */
    private String i18n;
    /**
     * AUTHORIZATION
     */
    private String authorization;
    /**
     * 请求种类
     */
    private RequestType requestType = RequestType.FROM_OUTSIDE;

    @Override
    public String toString() {
        return CommonConstants.EMPTY;
    }
}
