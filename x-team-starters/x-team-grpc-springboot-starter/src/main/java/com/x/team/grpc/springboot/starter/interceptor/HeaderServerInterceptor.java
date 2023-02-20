package com.x.team.grpc.springboot.starter.interceptor;

import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.grpc.springboot.starter.config.GrpcHeaderConfig;
import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 描述：Server grpc header interceptor
 *
 * @author MassAdobe
 * @date Created in 2023/2/15 15:13
 */
@Slf4j
@Component
public class HeaderServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // 过滤 envoy的配置  健康检查 接口
        if (!CollectionUtils.isEmpty(GrpcHeaderConfig.SYSTEM_EXCEPT_METHOD)
                && GrpcHeaderConfig.SYSTEM_EXCEPT_METHOD.stream()
                .anyMatch(str -> ServerGrpcInterceptor.MATCHER.match(str, serverCall.getMethodDescriptor().getFullMethodName()))) {
            // 处理健康检查 和 envoy注册发现
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(serverCallHandler.startCall(serverCall, requestHeaders)) {
            };
        }
        // 其他正常调用
        return serverCallHandler.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {

            @Override
            public void sendHeaders(Metadata responseHeaders) {
                UserContext userContext = HeaderContext.getContext();
                // 响应客户端设置服务端Header信息
                responseHeaders.put(GrpcHeaderConfig.GRPC_X_STEP_ID_HEADER, userContext.getRequestContext().getStepId());
                super.sendHeaders(responseHeaders);
            }
        }, requestHeaders);
    }
}
