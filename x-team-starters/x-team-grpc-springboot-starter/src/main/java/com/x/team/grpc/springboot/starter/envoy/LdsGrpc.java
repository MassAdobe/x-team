package com.x.team.grpc.springboot.starter.envoy;

import com.google.protobuf.Any;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.ListenerConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.RouterConstants;
import com.x.team.grpc.springboot.starter.envoy.templates.ResponseMapperTemplate;
import com.x.team.grpc.springboot.starter.envoy.v3.Resources;
import com.x.team.grpc.springboot.starter.envoy.v3.XdsGrpc;
import com.x.team.grpc.springboot.starter.exception.GrpcException;
import io.envoyproxy.envoy.config.core.v3.Address;
import io.envoyproxy.envoy.config.core.v3.SocketAddress;
import io.envoyproxy.envoy.config.listener.v3.Filter;
import io.envoyproxy.envoy.config.listener.v3.FilterChain;
import io.envoyproxy.envoy.config.listener.v3.Listener;
import io.envoyproxy.envoy.extensions.filters.http.grpc_json_transcoder.v3.GrpcJsonTranscoder;
import io.envoyproxy.envoy.extensions.filters.http.grpc_web.v3.GrpcWeb;
import io.envoyproxy.envoy.extensions.filters.http.local_ratelimit.v3.LocalRateLimit;
import io.envoyproxy.envoy.extensions.filters.http.router.v3.Router;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpFilter;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.LocalReplyConfig;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.Rds;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.RequestIDExtension;
import io.envoyproxy.envoy.extensions.request_id.uuid.v3.UuidRequestIdConfig;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.listener.v3.ListenerDiscoveryServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.BoolValue;

import java.net.UnknownHostException;

/**
 * 描述：envoy的ListenerDS
 *
 * @author MassAdobe
 * @date Created in 2023/2/10 16:45
 */
@Slf4j
@GrpcService
public class LdsGrpc extends ListenerDiscoveryServiceGrpc.ListenerDiscoveryServiceImplBase {

    /**
     * 描述：deltaListeners
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:37
     */
    @Override
    public StreamObserver<DeltaDiscoveryRequest> deltaListeners(StreamObserver<DeltaDiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DeltaDiscoveryRequest>() {
            @Override
            public void onNext(DeltaDiscoveryRequest deltaDiscoveryRequest) {
                log.trace("[envoy-listener]: deltaListeners\n[request: {}]",
                        deltaDiscoveryRequest);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-listener]: deltaListeners - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-listener]: deltaListeners - onCompleted");
            }
        };
    }

    /**
     * 描述：streamListeners
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DiscoveryRequest> streamListeners(StreamObserver<DiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DiscoveryRequest>() {
            @Override
            public void onNext(DiscoveryRequest discoveryRequest) {
                DiscoveryResponse discoveryResponse = null;
                try {
                    discoveryResponse = LdsGrpc.registerListener();
                } catch (UnknownHostException e) {
                    log.error("[envoy-listener]: streamEndpoints \n [error: current get server ip error]");
                    throw new GrpcException(ErrorCodeMsg.ENVOY_ERROR);
                }
                log.trace("[envoy-listener]: streamListeners\n[request: {}], \n[response: {}]",
                        discoveryRequest, discoveryResponse);
                // return envoy
                responseStreamObserver.onNext(discoveryResponse);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-listener]: streamListeners - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-listener]: streamListeners - onCompleted");
            }
        };
    }

    /**
     * 描述：fetchListeners
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:37
     */
    @Override
    public void fetchListeners(DiscoveryRequest discoveryRequest, StreamObserver<DiscoveryResponse> responseObserver) {
        DiscoveryResponse discoveryResponse = null;
        try {
            discoveryResponse = LdsGrpc.registerListener();
        } catch (UnknownHostException e) {
            log.error("[envoy-listener]: fetchListeners \n [error: current get server ip error]");
            throw new GrpcException(ErrorCodeMsg.ENVOY_ERROR);
        }
        log.trace("[envoy-listener]: fetchListeners\n[request: {}], \n[response: {}]",
                discoveryRequest, discoveryResponse);
        responseObserver.onNext(discoveryResponse);
        responseObserver.onCompleted();
    }

    /**
     * 描述：return current server listener info
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    private static DiscoveryResponse registerListener() throws UnknownHostException {
        // gRPC转换器
        GrpcJsonTranscoder grpcJsonTranscoder =
                GrpcJsonTranscoder.newBuilder()
                        // pb文件地址
                        .setProtoDescriptor(EnvoyConstants.GRPC_PB_PATH
                                + GrpcServiceConfig.getApplicationName()
                                + EnvoyConstants.GRPC_PB_FILE_SUFFIX)
                        // 所有的gRPC服务列表
                        .addAllServices(GrpcServiceConfig.grpcServiceList)
                        .setAutoMapping(Boolean.TRUE)
                        .setConvertGrpcStatus(Boolean.TRUE)
                        .setPrintOptions(GrpcJsonTranscoder.PrintOptions.newBuilder()
                                .setAddWhitespace(Boolean.TRUE)
                                .setPreserveProtoFieldNames(Boolean.TRUE)
                                .setAlwaysPrintEnumsAsInts(Boolean.FALSE)
                                .setAlwaysPrintPrimitiveFields(Boolean.TRUE))
                        .build();
        // http connection manager
        HttpConnectionManager httpConnectionManager =
                HttpConnectionManager.newBuilder()
                        .setCodecType(HttpConnectionManager.CodecType.AUTO)
                        .setStatPrefix(GrpcServiceConfig.getApplicationName()
                                + ListenerConstants.HTTP_STAT_PREFIX_SUFFIX)
                        // 添加traceId, header: x-request-id
                        .setRequestIdExtension(RequestIDExtension.newBuilder()
                                .setTypedConfig(Any.pack(UuidRequestIdConfig.newBuilder()
                                        // 控制实现是否改变 UUID 以包含上述的跟踪采样决定
                                        .setPackTraceReason(BoolValue.newBuilder().setValue(Boolean.FALSE).build())
                                        // 设置是否使用 x-request-id 进行采样
                                        .setUseRequestIdForTraceSampling(BoolValue.newBuilder().setValue(Boolean.FALSE).build())
                                        .build())))
                        // 过滤重写返回消息
                        .setLocalReplyConfig(LocalReplyConfig.newBuilder()
                                // ----- 401 request unauthorized -----
                                .addMappers(ResponseMapperTemplate.response401())
                                // ----- 404 request not found -----
                                .addMappers(ResponseMapperTemplate.response404())
                                // ----- 415 request unsupported media type -----
                                .addMappers(ResponseMapperTemplate.response415())
                                // ----- 429 request rate limit  -----
                                .addMappers(ResponseMapperTemplate.response429())
                                // ----- 503 service unavailable -----
                                .addMappers(ResponseMapperTemplate.response503())
                                // ----- 504 request timeout -----
                                .addMappers(ResponseMapperTemplate.response504())
                        )
                        // LDS configuration
                        .setRds(Rds.newBuilder()
                                .setRouteConfigName(GrpcServiceConfig.getApplicationName()
                                        + RouterConstants.ROUTER_SUFFIX)
                                // 返回XDS服务配置
                                .setConfigSource(XdsGrpc.rdsClusterConfiguration())
                                .build())
                        // envoy.filters.http.grpc_web
                        .addHttpFilters(HttpFilter.newBuilder()
                                .setName(Resources.FILTER_ENVOY_GRPC_WEB)
                                .setTypedConfig(Any.pack(GrpcWeb.newBuilder().build()))
                                .build())
                        // envoy.filters.http.grpc_json_transcoder
                        .addHttpFilters(HttpFilter.newBuilder()
                                .setName(Resources.FILTER_ENVOY_GRPC_JSON_TRANSCODER)
                                .setTypedConfig(Any.pack(grpcJsonTranscoder))
                                .build())
                        // envoy.filters.http.local_ratelimit
                        .addHttpFilters(HttpFilter.newBuilder()
                                .setName(Resources.FILTER_ENVOY_LOCAL_RATELIMIT)
                                .setTypedConfig(Any.pack(LocalRateLimit.newBuilder()
                                        .setStatPrefix(GrpcServiceConfig.getApplicationName()
                                                + RouterConstants.ROUTE_REQUEST_LOCAL_LIMIT_SUFFIX).build()))
                                .build())
                        // envoy.filters.http.router
                        .addHttpFilters(HttpFilter.newBuilder()
                                .setName(Resources.FILTER_ENVOY_ROUTER)
                                .setTypedConfig(Any.pack(Router.newBuilder().build())))
                        .build();
        // listener
        Listener listener = Listener.newBuilder()
                .setName(GrpcServiceConfig.getApplicationName()
                        + ListenerConstants.LISTENER_SUFFIX)
                // address
                .setAddress(Address.newBuilder()
                        .setSocketAddress(SocketAddress.newBuilder()
                                // select environments, as native docker address, as others k8s dns
                                .setAddress(ListenerConstants.ANY_ADDRESS)
                                .setPortValue(ListenerConstants.LISTENER_PORT)
                                .setProtocol(SocketAddress.Protocol.TCP)))
                .addFilterChains(FilterChain.newBuilder()
                        // Http-Router
                        .addFilters(Filter.newBuilder()
                                .setName(Resources.FILTER_HTTP_CONNECTION_MANAGER)
                                .setTypedConfig(Any.pack(httpConnectionManager))))
                .build();
        // return envoy register listener
        return DiscoveryResponse.newBuilder()
                .setTypeUrl(Resources.V3.LISTENER_TYPE_URL)
                .setVersionInfo(EnvoyConstants.CONFIGURATION_VERSION)
                .addResources(Any.pack(listener))
                .build();
    }
}
