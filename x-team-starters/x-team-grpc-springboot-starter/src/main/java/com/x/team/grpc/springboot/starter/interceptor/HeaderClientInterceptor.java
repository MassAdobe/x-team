package com.x.team.grpc.springboot.starter.interceptor;

import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.common.enums.RequestType;
import com.x.team.grpc.springboot.starter.config.GrpcHeaderConfig;
import com.x.team.grpc.springboot.starter.exception.GrpcException;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 描述：头处理
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:10
 */
@Slf4j
@Component
@GrpcGlobalClientInterceptor
public class HeaderClientInterceptor implements ClientInterceptor {

    @Autowired
    private GrpcHeaderConfig grpcHeaderConfig;

    @SuppressWarnings("all")
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata metadata) {
                // 获取HEADER信息（maven rest引用）
                UserContext userContext = HeaderContext.getContext();
                // 判断GRPC请求头是否为空
                if (Objects.isNull(userContext)) {
                    log.error("\n[grpc-client]: current request header context empty error");
                    throw new GrpcException(ErrorCodeMsg.GRPC_ERROR);
                } else {
                    // 判断GRPC请求头是否为空-否（maven rest引用）
                    if (!StringUtils.isEmpty(userContext.getRequestContext().getAuthorization())) {
                        metadata.put(grpcHeaderConfig.GRPC_AUTHORIZATION_HEADER,
                                userContext.getRequestContext().getAuthorization());
                    }
                    if (!StringUtils.isEmpty(userContext.getRequestContext().getTraceId())) {
                        metadata.put(grpcHeaderConfig.GRPC_X_REQUEST_ID_HEADER,
                                userContext.getRequestContext().getTraceId());
                    }
                    if (!StringUtils.isEmpty(userContext.getRequestContext().getStepId())) {
                        metadata.put(grpcHeaderConfig.GRPC_X_STEP_ID_HEADER,
                                userContext.getRequestContext().getStepId());
                    }
                    if (!StringUtils.isEmpty(userContext.getRequestContext().getI18n())) {
                        metadata.put(grpcHeaderConfig.GRPC_ACCEPT_LANGUAGE_HEADER,
                                userContext.getRequestContext().getI18n());
                    }
                    if (RequestType.SCHEDULE_CALL.equals(userContext.getRequestContext().getRequestType())) {
                        // 如果是schedule
                        metadata.put(grpcHeaderConfig.GRPC_REQUEST_TYPE_HEADER,
                                RequestType.SCHEDULE_CALL.getCode());
                    } else {
                        // 其他都是inner call
                        metadata.put(grpcHeaderConfig.GRPC_REQUEST_TYPE_HEADER,
                                RequestType.INNER_CALL.getCode());
                    }
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    /**
                     * 打印调用后返回的头信息
                     */
                    @Override
                    public void onHeaders(Metadata metadata) {
                        // 设置当前stepId
                        Integer stepId = Integer.valueOf(metadata.get(grpcHeaderConfig.GRPC_X_STEP_ID_HEADER));
                        MDC.put(TraceConstants.STEP_ID, String.valueOf(++stepId));
                        // 日志
                        log.info("\n[grpc-client]: \n[server-metadata: {}], \n[server-name: {}], \n[method-name: {}]",
                                metadata, method.getServiceName(), method.getFullMethodName());
                        // 重置全局信息
                        UserContext userContext = HeaderContext.getContext();
                        userContext.getRequestContext().setStepId(String.valueOf(stepId));
                        HeaderContext.setContext(userContext);
                        super.onHeaders(metadata);
                    }

                    /**
                     * 拦截打印调用返回的错误
                     */
                    @Override
                    public void onClose(Status status, Metadata metadata) {
                        if (Status.Code.OK == status.getCode()) {
                            // 正确返回
                            log.info("\n[grpc-client]: \n[server-status: {}] \n[server-metadata: {}], \n[server-name: {}], \n[method-name: {}]",
                                    status, metadata, method.getServiceName(), method.getFullMethodName());
                        } else {
                            // 错误返回
                            log.error("\n[grpc-client]: \n[server-status: {}] \n[server-metadata: {}], \n[server-name: {}], \n[method-name: {}]",
                                    status, metadata, method.getServiceName(), method.getFullMethodName());
                        }
                        super.onClose(status, metadata);
                    }
                }, metadata);
            }
        };
    }
}
