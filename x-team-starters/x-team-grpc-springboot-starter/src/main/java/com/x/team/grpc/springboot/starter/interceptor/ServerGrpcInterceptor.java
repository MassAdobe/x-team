package com.x.team.grpc.springboot.starter.interceptor;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import com.x.team.common.constants.CloudServerConstants;
import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.common.constants.LanguageConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.RequestContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.dto.rest.NoticeDto;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.common.enums.RequestType;
import com.x.team.grpc.springboot.starter.config.GrpcHeaderConfig;
import com.x.team.grpc.springboot.starter.config.RSAConfig;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import com.x.team.log.springboot.starter.utils.XTeamLog;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 描述：Server grpc interceptor
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:11
 */
@Slf4j
@Component
public class ServerGrpcInterceptor implements ServerInterceptor {

    /**
     * 秘钥信息
     */
    @Autowired
    private RSAConfig rsaConfig;

    /**
     * 注入GRPC头信息
     */
    @Autowired
    private GrpcHeaderConfig grpcHeaderConfig;

    /**
     * URL PATH MATCHER
     */
    public final static AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    @SuppressWarnings("all")
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // 过滤 envoy的配置  健康检查 接口
        if (!CollectionUtils.isEmpty(GrpcHeaderConfig.SYSTEM_EXCEPT_METHOD)
                && GrpcHeaderConfig.SYSTEM_EXCEPT_METHOD.stream()
                .anyMatch(str -> MATCHER.match(str, serverCall.getMethodDescriptor().getFullMethodName()))) {
            // 处理健康检查 和 envoy注册发现
            return ServerGrpcInterceptor.healthEnvoyHandler(serverCall, serverCallHandler, metadata);
        }
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(serverCallHandler.startCall(serverCall, metadata)) {

            /**
             * 处理开始时间
             */
            private long startTime = 0;

            /**
             * 请求头
             */
            private RequestContext requestContext;

            @Override
            public void onMessage(ReqT message) {
                // 设置开始时间
                this.startTime = System.currentTimeMillis();
                // 国际化语言
                String acceptLanguage = StringUtils.isEmpty(metadata.get(GrpcHeaderConfig.GRPC_ACCEPT_LANGUAGE_HEADER))
                        ? LanguageConstants.ZH_CN : metadata.get(GrpcHeaderConfig.GRPC_ACCEPT_LANGUAGE_HEADER);
                // AUTHORIZATION
                String authorization = StringUtils.isEmpty(metadata.get(GrpcHeaderConfig.GRPC_AUTHORIZATION_HEADER))
                        ? CommonConstants.EMPTY : metadata.get(GrpcHeaderConfig.GRPC_AUTHORIZATION_HEADER);
                // REQUEST TYPE
                RequestType requestType = StringUtils.isEmpty(metadata.get(GrpcHeaderConfig.GRPC_REQUEST_TYPE_HEADER))
                        ? RequestType.FROM_OUTSIDE : RequestType.getRequestType(metadata.get(GrpcHeaderConfig.GRPC_REQUEST_TYPE_HEADER));
                // 设置GRPC的头 - 日志追踪
                if (StringUtils.isEmpty(metadata.get(GrpcHeaderConfig.GRPC_X_REQUEST_ID_HEADER))) {
                    XTeamLog.setBegin();
                } else {
                    if (StringUtils.isEmpty(metadata.get(GrpcHeaderConfig.GRPC_X_STEP_ID_HEADER))) {
                        XTeamLog.setBegin(metadata.get(GrpcHeaderConfig.GRPC_X_REQUEST_ID_HEADER));
                    } else {
                        int step = Integer.parseInt(metadata.get(GrpcHeaderConfig.GRPC_X_STEP_ID_HEADER));
                        XTeamLog.setBegin(metadata.get(GrpcHeaderConfig.GRPC_X_REQUEST_ID_HEADER), String.valueOf(++step));
                    }
                }
                // 设置请求头
                this.requestContext = RequestContext.builder()
                        .traceId(MDC.get(TraceConstants.TRACE_ID))
                        .stepId(MDC.get(TraceConstants.STEP_ID))
                        .i18n(acceptLanguage)
                        .authorization(authorization)
                        .requestType(requestType)
                        .build();
                // 设置全局信息
                HeaderContext.setContext(checkRequestType(serverCall, this.requestContext, acceptLanguage, requestType, authorization));
                if (!HeaderContext.existContext()) {
                    log.error("\n[grpc-server]: request header display error: \n[server-name: {}], \n[method-name: {}]]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName());
                    throw new RuntimeException(ErrorCodeMsg.NO_HEADER_ERROR.getZhMessage());
                }
                // 获取当前请求种类
                if (RequestType.FROM_OUTSIDE.equals(requestType)) {
                    // 打印gRPC请求日志 from outside
                    log.info("\n[grpc-server]: from-outside-request-info: \n[server-name: {}], \n[method-name: {}], \n[params: \n{}]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            message.toString());
                } else if (RequestType.SCHEDULE_CALL.equals(requestType)) {
                    // 打印gRPC请求日志 schedule call
                    log.info("\n[grpc-server]: schedule-call-request-info: \n[server-name: {}], \n[method-name: {}], \n[params: \n{}]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            message.toString());
                } else {
                    // 打印gRPC请求日志 inner call
                    log.info("\n[grpc-server]: inner-call-request-info: \n[server-name: {}], \n[method-name: {}], \n[params: \n{}]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            message.toString());
                }
                super.onMessage(message);
            }

            @Override
            public void onComplete() {
                if (XTeamLog.existTrace()) {
                    MDC.put(TraceConstants.TRACE_ID, this.requestContext.getTraceId());
                    MDC.put(TraceConstants.STEP_ID, this.requestContext.getStepId());
                }
                // 打印响应日志
                log.info("\n[grpc-server]: response-info: \n[server-name: {}], \n[method-name: {}], \n[user: {}], \n[handle-period: {}ms]",
                        serverCall.getMethodDescriptor().getServiceName(),
                        serverCall.getMethodDescriptor().getFullMethodName(),
                        HeaderContext.getContext(),
                        (System.currentTimeMillis() - this.startTime));
                XTeamLog.removeBegin();
                HeaderContext.clearContext();
                super.onComplete();
            }
        };
    }

    /**
     * 处理健康检查 和 envoy注册发现
     */
    public static <ReqT, RespT> ServerCall.Listener<ReqT> healthEnvoyHandler(ServerCall<ReqT, RespT> serverCall,
                                                                             ServerCallHandler<ReqT, RespT> serverCallHandler,
                                                                             Metadata metadata) {
        if (GrpcHeaderConfig.HEALTH_METHOD.equals(serverCall.getMethodDescriptor().getFullMethodName())) {
            // 当前是健康检查
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(serverCallHandler.startCall(serverCall, metadata)) {

                @Override
                public void onMessage(ReqT message) {
                    // 打印GRPC请求日志 health check
                    log.trace("\n[grpc-server]: health-check-request-info: \n[server-name: {}], \n[method-name: {}], \n[params: \n{}]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            message.toString());
                    super.onMessage(message);
                }
            };
        } else {
            // 当前是envoy获取数据面板信息
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(serverCallHandler.startCall(serverCall, metadata)) {

                @Override
                public void onMessage(ReqT message) {
                    // 打印GRPC请求日志 envoy
                    log.trace("\n[grpc-server]: envoy-request-info: \n[server-name: {}], \n[method-name: {}], \n[params: \n{}]",
                            serverCall.getMethodDescriptor().getServiceName(),
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            message.toString());
                    super.onMessage(message);
                }
            };
        }
    }

    /**
     * 解析请求种类
     */
    @SuppressWarnings("all")
    private UserContext checkRequestType(ServerCall serverCall,
                                         RequestContext requestContext,
                                         String acceptLanguage,
                                         RequestType requestType,
                                         String authorization) {
        // 请求种类
        if (requestType.equals(RequestType.NO_HEADER)) {
            // 如果是不需要头的请求种类(例如：kafka线程池方法) 不需要解析头 设置全局用户信息 需要声明式设置头
            return UserContext.builder()
                    .requestContext(requestContext)
                    .build();
        } else if (requestType.equals(RequestType.INNER_CALL)) {
            // 如果是内部调用的请求种类(例如：内部调用) 需要解析头 解析AUTHORIZATION
        } else if (requestType.equals(RequestType.SCHEDULE_CALL)) {
            // 如果是定时任务请求，不需要解析头
            return UserContext.builder()
                    .requestContext(requestContext)
                    .build();
        } else {
            // 过滤是否需要AUTHORIZATION - 需要
        }
        // 解析AUTHORIZATION
        return this.decodeAuthorization(serverCall, requestContext, acceptLanguage, authorization);
    }

    /**
     * 解析AUTHORIZATION
     */
    @SuppressWarnings("all")
    private UserContext decodeAuthorization(ServerCall serverCall,
                                            RequestContext requestContext,
                                            String acceptLanguage,
                                            String authorization) {
        // 解析token
        String token;
        try {
            // TODO
            // token = EncryptUtils.serverDecrypt(authorization, this.rsaConfig.getPrivateRsa());
            // TODO
//            log.info("\n[grpc-server]: request-user: \n[user: user-id: {}, real-name: {}]",
//                    userContext.getUserId(), userContext.getRealName());
            // TODO 设置全局用户信息
            return UserContext.builder()
                    .userId("abc")
                    .realName("测试")
                    .requestContext(requestContext)
                    .build();
        } catch (Exception e) {
            log.error("\n[grpc-server]: request token decrypt error: \n[server-name: {}], \n[method-name: {}], \n[error: {}]\n]",
                    serverCall.getMethodDescriptor().getServiceName(),
                    serverCall.getMethodDescriptor().getFullMethodName(),
                    e.getMessage());
            NoticeDto noticeDto = ErrorCodeMsg.getError(acceptLanguage, ErrorCodeMsg.SYSTEM_TOKEN_DECODE_ERROR);
            StatusRuntimeException statusRuntimeException = StatusProto.toStatusRuntimeException(Status.newBuilder()
                    .setCode(Code.UNAUTHENTICATED.getNumber())
                    .setMessage(noticeDto.getMsg())
                    .addDetails(Any.pack(ErrorInfo.newBuilder()
                            .setDomain(CloudServerConstants.DOMAIN)
                            .setReason(GrpcConstants.GRPC_RETURN_SYSTEM_ERROR)
                            .putMetadata(GrpcConstants.ERROR_STATUS, noticeDto.getStatus())
                            .putMetadata(GrpcConstants.ERROR_MESSAGE, noticeDto.getMsg())
                            .build()))
                    .build());
            serverCall.close(statusRuntimeException.getStatus(), statusRuntimeException.getTrailers());
            XTeamLog.removeBegin();
            throw new RuntimeException(ErrorCodeMsg.SYSTEM_TOKEN_DECODE_ERROR.getZhMessage());
        }
    }

}
