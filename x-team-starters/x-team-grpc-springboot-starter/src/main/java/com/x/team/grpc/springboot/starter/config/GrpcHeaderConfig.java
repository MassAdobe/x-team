package com.x.team.grpc.springboot.starter.config;

import com.google.common.collect.Lists;
import com.x.team.common.constants.GrpcConstants;
import io.grpc.Metadata;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：gRPC的头配置
 *
 * @author MassAdobe
 * @date Created in 2023/2/7 19:07
 */
@Component
@RefreshScope
public class GrpcHeaderConfig {

    /**
     * 健康检查方法名
     */
    public final static String HEALTH_METHOD = "grpc.health.v1.Health/*";
    /**
     * 健康检查方法名(k8s)
     */
    public final static String HEALTH_SERVER_METHOD = "grpc.health.server.Health/*";

    /**
     * ENVOY LISTENER METHOD
     */
    private final static String ENVOY_LISTENER_METHOD = "envoy.service.listener.v3.ListenerDiscoveryService/*Listeners";

    /**
     * ENVOY ROUTER METHOD
     */
    private final static String ENVOY_ROUTER_METHOD = "envoy.service.route.v3.RouteDiscoveryService/*Routes";

    /**
     * ENVOY CLUSTER METHOD
     */
    private final static String ENVOY_CLUSTER_METHOD = "envoy.service.cluster.v3.ClusterDiscoveryService/*Clusters";

    /**
     * ENVOY ENDPOINT METHOD
     */
    private final static String ENVOY_ENDPOINT_METHOD = "envoy.service.endpoint.v3.EndpointDiscoveryService/*Endpoints";

    /**
     * envoy获取配置方法列表
     */
    public final static List<String> SYSTEM_EXCEPT_METHOD = Lists.newArrayList(
            // envoy methods
            ENVOY_LISTENER_METHOD, ENVOY_ROUTER_METHOD, ENVOY_CLUSTER_METHOD, ENVOY_ENDPOINT_METHOD,
            // gRPC health method
            GrpcHeaderConfig.HEALTH_METHOD, GrpcHeaderConfig.HEALTH_SERVER_METHOD
    );

    /**
     * header key authorization
     */
    public static final Metadata.Key<String> GRPC_AUTHORIZATION_HEADER = Metadata.Key.of(GrpcConstants.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);

    /**
     * header key x_request_id
     */
    public static final Metadata.Key<String> GRPC_X_REQUEST_ID_HEADER = Metadata.Key.of(GrpcConstants.X_REQUEST_ID, Metadata.ASCII_STRING_MARSHALLER);

    /**
     * header key x_request_step
     */
    public static final Metadata.Key<String> GRPC_X_STEP_ID_HEADER = Metadata.Key.of(GrpcConstants.X_STEP_ID, Metadata.ASCII_STRING_MARSHALLER);

    /**
     * header key accept-language
     */
    public static final Metadata.Key<String> GRPC_ACCEPT_LANGUAGE_HEADER = Metadata.Key.of(GrpcConstants.ACCEPT_LANGUAGE, Metadata.ASCII_STRING_MARSHALLER);

    /**
     * header key request-type
     */
    public static final Metadata.Key<String> GRPC_REQUEST_TYPE_HEADER = Metadata.Key.of(GrpcConstants.REQUEST_TYPE, Metadata.ASCII_STRING_MARSHALLER);
}
