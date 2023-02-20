package com.x.team.grpc.springboot.starter.envoy;

import com.google.protobuf.Any;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.RouterConstants;
import com.x.team.grpc.springboot.starter.envoy.templates.RouteTemplate;
import com.x.team.grpc.springboot.starter.envoy.v3.Resources;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration;
import io.envoyproxy.envoy.config.route.v3.VirtualHost;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.route.v3.RouteDiscoveryServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 描述：envoy的RouterDS
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 16:21
 */
@Slf4j
@GrpcService
public class RdsGrpc extends RouteDiscoveryServiceGrpc.RouteDiscoveryServiceImplBase {

    /**
     * 描述：deltaRoutes
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DeltaDiscoveryRequest> deltaRoutes(StreamObserver<DeltaDiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DeltaDiscoveryRequest>() {
            @Override
            public void onNext(DeltaDiscoveryRequest deltaDiscoveryRequest) {
                log.trace("[envoy-router]: deltaRoutes\n[request: {}]",
                        deltaDiscoveryRequest);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-router]: deltaRoutes - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-router]: deltaRoutes - onCompleted");
            }
        };
    }

    /**
     * 描述：streamRoutes
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DiscoveryRequest> streamRoutes(StreamObserver<DiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DiscoveryRequest>() {
            @Override
            public void onNext(DiscoveryRequest discoveryRequest) {
                DiscoveryResponse discoveryResponse = RdsGrpc.registerRouter();
                log.trace("[envoy-router]: streamRoutes\n[request: {}], \n[response: {}]",
                        discoveryRequest, discoveryResponse);
                // return envoy
                responseStreamObserver.onNext(discoveryResponse);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-router]: streamRoutes - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-router]: streamRoutes - onCompleted");
            }
        };
    }

    /**
     * 描述：fetchRoutes
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public void fetchRoutes(DiscoveryRequest discoveryRequest, StreamObserver<DiscoveryResponse> responseObserver) {
        DiscoveryResponse discoveryResponse = RdsGrpc.registerRouter();
        log.trace("[envoy-router]: fetchRoutes\n[request: {}], \n[response: {}]",
                discoveryRequest, discoveryResponse);
        responseObserver.onNext(discoveryResponse);
        responseObserver.onCompleted();
    }

    /**
     * 描述：return current server router info
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    private static DiscoveryResponse registerRouter() {
        // router
        RouteConfiguration routeConfiguration = RouteConfiguration.newBuilder()
                .setName(GrpcServiceConfig.getApplicationName()
                        + RouterConstants.ROUTER_SUFFIX)
                // 添加响应头
                .addResponseHeadersToAdd(HeaderValueOption.newBuilder()
                        // 透传请求的traceId
                        .setHeader(HeaderValue.newBuilder()
                                .setKey(GrpcConstants.X_REQUEST_ID)
                                .setValue(RouterConstants.ROUTER_REQUEST_ID_VALUE)
                                .build())
                        // 如果存在 就重写 不存在 就加入
                        .setAppendAction(HeaderValueOption.HeaderAppendAction.OVERWRITE_IF_EXISTS_OR_ADD)
                        .build())
                // virtual hosts
                .addVirtualHosts(VirtualHost.newBuilder()
                        .setName(GrpcServiceConfig.getApplicationName()
                                + RouterConstants.VIRTUAL_HOSTS_SUFFIX)
                        // ["*"]
                        .addDomains(RouterConstants.ROUTE_DOMAIN_ALL)
                        // 添加路由
                        .addAllRoutes(RouteTemplate.generateRoutes()))
                .build();
        // return envoy register router
        return DiscoveryResponse.newBuilder()
                .setTypeUrl(Resources.V3.ROUTE_TYPE_URL)
                .setVersionInfo(EnvoyConstants.CONFIGURATION_VERSION)
                .addResources(Any.pack(routeConfiguration))
                .build();
    }

}
