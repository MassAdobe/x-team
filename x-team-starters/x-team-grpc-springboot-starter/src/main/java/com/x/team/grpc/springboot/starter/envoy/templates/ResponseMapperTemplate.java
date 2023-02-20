package com.x.team.grpc.springboot.starter.envoy.templates;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.util.Structs;
import com.google.protobuf.util.Values;
import com.x.team.common.constants.CloudServerConstants;
import com.x.team.common.constants.HttpConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.grpc.springboot.starter.envoy.constants.ListenerConstants;
import io.envoyproxy.envoy.config.accesslog.v3.AccessLogFilter;
import io.envoyproxy.envoy.config.accesslog.v3.ComparisonFilter;
import io.envoyproxy.envoy.config.accesslog.v3.StatusCodeFilter;
import io.envoyproxy.envoy.config.core.v3.RuntimeUInt32;
import io.envoyproxy.envoy.config.core.v3.SubstitutionFormatString;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.ResponseMapper;

/**
 * 描述：LocalReplyConfig定义模板
 *
 * @author MassAdobe
 * @date Created in 2023/2/16 19:24
 */
public class ResponseMapperTemplate {


    /**
     * 描述：UNAUTHORIZED RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:33
     */
    public static ResponseMapper response401() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "18",
         *                 "errorMsg": "UNAUTHORIZED ERROR"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 401
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_UNAUTHORIZED)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 401
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_UNAUTHORIZED))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.UNAUTHORIZED_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.UNAUTHORIZED_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

    /**
     * 描述：NOT FOUND RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:33
     */
    public static ResponseMapper response404() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "18",
         *                 "errorMsg": "UNAUTHORIZED ERROR"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 404
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_NOT_FOUND)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 404
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_NOT_FOUND))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.NOT_FOUND_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.NOT_FOUND_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

    /**
     * 描述：UNSUPPORTED MEDIA TYPE RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:33
     */
    public static ResponseMapper response415() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "21",
         *                 "errorMsg": "UNSUPPORTED  MEDIA TYPE ERROR"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 404
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_UNSUPPORTED_MEDIA_TYPE)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 404
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_UNSUPPORTED_MEDIA_TYPE))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.UNSUPPORTED_MEDIA_TYPE_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.UNSUPPORTED_MEDIA_TYPE_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

    /**
     * 描述：RATE LIMIT RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:25
     */
    public static ResponseMapper response429() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "15",
         *                 "errorMsg": "RATE LIMIT"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 429
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_STATUS_LIMIT)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 429
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_STATUS_LIMIT))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.RATE_LIMIT_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.RATE_LIMIT_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

    /**
     * 描述：SERVICE UNAVAILABLE RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:31
     */
    public static ResponseMapper response503() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "20",
         *                 "errorMsg": "SERVICE UNAVAILABLE ERROR"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 503
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_SERVICE_UNAVAILABLE)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 503
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_SERVICE_UNAVAILABLE))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.SERVICE_UNAVAILABLE_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.SERVICE_UNAVAILABLE_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

    /**
     * 描述：TIME OUT RESPONSE
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 19:31
     */
    public static ResponseMapper response504() {
        /*
         * return json body
         * <p/>
         * {
         *     "code": "2",
         *     "message": "SYSTEM ERROR",
         *     "details": [
         *         {
         *             "@type": "type.googleapis.com/google.rpc.ErrorInfo",
         *             "reason": "system-error",
         *             "domain": "X-TEAM",
         *             "metadata": {
         *                 "errorStatus": "14",
         *                 "errorMsg": "REQUEST TIMEOUT"
         *             }
         *         }
         *     ]
         * }
         */
        return ResponseMapper.newBuilder()
                // 设置过滤器
                .setFilter(AccessLogFilter.newBuilder()
                        // filter 504
                        .setStatusCodeFilter(StatusCodeFilter.newBuilder()
                                .setComparison(ComparisonFilter.newBuilder()
                                        // 等于
                                        .setOp(ComparisonFilter.Op.EQ)
                                        .setValue(RuntimeUInt32.newBuilder()
                                                .setDefaultValue(HttpConstants.HTTP_STATUS_TIMEOUT)
                                                .setRuntimeKey(ListenerConstants.RUNTIME_KEY)))))
                // return 504
                .setStatusCode(UInt32Value.of(HttpConstants.HTTP_STATUS_TIMEOUT))
                // return json body
                .setBodyFormatOverride(SubstitutionFormatString.newBuilder()
                        .setJsonFormat(Structs.of(
                                "code", Values.of("2"),
                                "message", Values.of("SYSTEM ERROR"),
                                "details", Values.of(ListValue.newBuilder()
                                        .addValues(Values.of(Struct.newBuilder()
                                                .putFields("@type", Values.of("type.googleapis.com/google.rpc.ErrorInfo"))
                                                .putFields("reason", Values.of("system-error"))
                                                .putFields("domain", Values.of(CloudServerConstants.DOMAIN))
                                                .putFields("metadata", Values.of(Structs.of(
                                                        "errorStatus", Values.of(ErrorCodeMsg.TIMEOUT_ERROR.getStatus()),
                                                        "errorMsg", Values.of(ErrorCodeMsg.TIMEOUT_ERROR.getEnMessage())
                                                ))).build())).build()))))
                .build();
    }

}
