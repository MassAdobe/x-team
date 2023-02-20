package com.x.team.grpc.springboot.starter.envoy;

import com.google.protobuf.Any;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.util.Durations;
import com.x.team.common.constants.SystemConstants;
import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.envoy.constants.ClusterConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import com.x.team.grpc.springboot.starter.envoy.v3.Resources;
import com.x.team.grpc.springboot.starter.envoy.v3.XdsGrpc;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.config.core.v3.Address;
import io.envoyproxy.envoy.config.core.v3.Http2ProtocolOptions;
import io.envoyproxy.envoy.config.core.v3.SocketAddress;
import io.envoyproxy.envoy.config.core.v3.UpstreamHttpProtocolOptions;
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment;
import io.envoyproxy.envoy.config.endpoint.v3.Endpoint;
import io.envoyproxy.envoy.config.endpoint.v3.LbEndpoint;
import io.envoyproxy.envoy.config.endpoint.v3.LocalityLbEndpoints;
import io.envoyproxy.envoy.extensions.upstreams.http.v3.HttpProtocolOptions;
import io.envoyproxy.envoy.service.cluster.v3.ClusterDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 描述：envoy的ClusterDS
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 18:28
 */
@Slf4j
@GrpcService
public class CdsGrpc extends ClusterDiscoveryServiceGrpc.ClusterDiscoveryServiceImplBase {

    /**
     * 描述：deltaClusters
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DeltaDiscoveryRequest> deltaClusters(StreamObserver<DeltaDiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DeltaDiscoveryRequest>() {
            @Override
            public void onNext(DeltaDiscoveryRequest deltaDiscoveryRequest) {
                log.trace("[envoy-cluster]: deltaClusters\n[request: {}]",
                        deltaDiscoveryRequest);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-cluster]: deltaClusters - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-cluster]: deltaClusters - onCompleted");
            }
        };
    }

    /**
     * 描述：streamClusters
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DiscoveryRequest> streamClusters(StreamObserver<DiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DiscoveryRequest>() {
            @Override
            public void onNext(DiscoveryRequest discoveryRequest) {
                DiscoveryResponse discoveryResponse = CdsGrpc.registerCluster();
                log.trace("[envoy-cluster]: streamClusters\n[request: {}], \n[response: {}]",
                        discoveryRequest, discoveryResponse);
                // return envoy
                responseStreamObserver.onNext(discoveryResponse);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-cluster]: streamClusters - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-cluster]: streamClusters - onCompleted");
            }
        };
    }

    /**
     * 描述：fetchClusters
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public void fetchClusters(DiscoveryRequest discoveryRequest, StreamObserver<DiscoveryResponse> responseObserver) {
        DiscoveryResponse discoveryResponse = CdsGrpc.registerCluster();
        log.trace("[envoy-cluster]: fetchClusters\n[request: {}], \n[response: {}]",
                discoveryRequest, discoveryResponse);
        responseObserver.onNext(discoveryResponse);
        responseObserver.onCompleted();
    }

    /**
     * 描述：return current server cluster info
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @SuppressWarnings("all")
    private static DiscoveryResponse registerCluster() {
        // 初始化Cluster
        Cluster cluster;
        // cluster - static
        if (SystemConstants.ENVIRONMENT_NATIVE.equals(GrpcServiceConfig.environment)) {
            cluster = Cluster.newBuilder()
                    .setName(GrpcServiceConfig.getApplicationName()
                            + ClusterConstants.CLUSTER_SUFFIX)
                    .setConnectTimeout(Durations.fromSeconds(60))
                    .setType(Cluster.DiscoveryType.LOGICAL_DNS)
                    // PROTOCOL扩展
                    .putTypedExtensionProtocolOptions(Resources.UPSTREAM_EXTENSION_HTTP_PROTOCOL_OPTIONS,
                            Any.pack(HttpProtocolOptions.newBuilder()
                                    .setUpstreamHttpProtocolOptions(UpstreamHttpProtocolOptions.newBuilder().setAutoSni(Boolean.TRUE))
                                    .setCommonHttpProtocolOptions(
                                            io.envoyproxy.envoy.config.core.v3.HttpProtocolOptions.newBuilder().setIdleTimeout(
                                                    Durations.fromSeconds(60)))
                                    .setExplicitHttpConfig(HttpProtocolOptions.ExplicitHttpConfig.newBuilder()
                                            .setHttp2ProtocolOptions(Http2ProtocolOptions.newBuilder()
                                                    .setMaxConcurrentStreams(UInt32Value.newBuilder().setValue(65536))
                                                    .setInitialStreamWindowSize(UInt32Value.newBuilder().setValue(65536))
                                                    .setInitialConnectionWindowSize(UInt32Value.newBuilder().setValue(1048576))))
                                    .build()))
                    .setLoadAssignment(ClusterLoadAssignment.newBuilder()
                            .setClusterName(GrpcServiceConfig.getApplicationName()
                                    + ClusterConstants.CLUSTER_SUFFIX)
                            // Locality-LB-Endpoint
                            .addEndpoints(LocalityLbEndpoints.newBuilder()
                                    // LB-Endpoint
                                    .addLbEndpoints(LbEndpoint.newBuilder()
                                            // Endpoint
                                            .setEndpoint(Endpoint.newBuilder()
                                                    // Address
                                                    .setAddress(Address.newBuilder()
                                                            .setSocketAddress(SocketAddress.newBuilder()
                                                                    // address select environments, as native docker address, as others k8s dns
                                                                    .setAddress(SystemConstants.ENVIRONMENT_NATIVE.equals(GrpcServiceConfig.getEnvironment()) ?
                                                                            EnvoyConstants.DOCKER_ADDRESS : GrpcServiceConfig.getApplicationName())
                                                                    // port
                                                                    .setPortValue(GrpcServiceConfig.getGrpcServerPort())
                                                                    // protocol
                                                                    .setProtocolValue(SocketAddress.Protocol.TCP_VALUE)
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
                    .build();
        } else {
            // cluster - eds
            cluster = Cluster.newBuilder()
                    .setName(GrpcServiceConfig.getApplicationName()
                            + ClusterConstants.CLUSTER_SUFFIX)
                    .setConnectTimeout(Durations.fromSeconds(60))
                    .setType(Cluster.DiscoveryType.EDS)
                    // PROTOCOL扩展
                    .putTypedExtensionProtocolOptions(Resources.UPSTREAM_EXTENSION_HTTP_PROTOCOL_OPTIONS,
                            Any.pack(HttpProtocolOptions.newBuilder()
                                    .setUpstreamHttpProtocolOptions(UpstreamHttpProtocolOptions.newBuilder().setAutoSni(Boolean.TRUE))
                                    .setCommonHttpProtocolOptions(
                                            io.envoyproxy.envoy.config.core.v3.HttpProtocolOptions.newBuilder().setIdleTimeout(
                                                    Durations.fromSeconds(60)))
                                    .setExplicitHttpConfig(HttpProtocolOptions.ExplicitHttpConfig.newBuilder()
                                            .setHttp2ProtocolOptions(Http2ProtocolOptions.newBuilder()
                                                    .setMaxConcurrentStreams(UInt32Value.newBuilder().setValue(65536))
                                                    .setInitialStreamWindowSize(UInt32Value.newBuilder().setValue(65536))
                                                    .setInitialConnectionWindowSize(UInt32Value.newBuilder().setValue(1048576))))
                                    .build()))
                    .setLbPolicy(Cluster.LbPolicy.ROUND_ROBIN)
                    .setEdsClusterConfig(Cluster.EdsClusterConfig.newBuilder()
                            .setEdsConfig(XdsGrpc.edsClusterConfiguration()))
                    .build();
        }
        // return envoy register cluster
        return DiscoveryResponse.newBuilder()
                .setTypeUrl(Resources.V3.CLUSTER_TYPE_URL)
                .setVersionInfo(EnvoyConstants.CONFIGURATION_VERSION)
                .addResources(Any.pack(cluster))
                .build();
    }

}
