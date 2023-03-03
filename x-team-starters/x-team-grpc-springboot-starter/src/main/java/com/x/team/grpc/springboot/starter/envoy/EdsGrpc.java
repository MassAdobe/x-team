package com.x.team.grpc.springboot.starter.envoy;

import com.google.protobuf.Any;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.envoy.constants.ClusterConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.ListenerConstants;
import com.x.team.grpc.springboot.starter.envoy.v3.Resources;
import com.x.team.grpc.springboot.starter.exception.GrpcException;
import io.envoyproxy.envoy.config.core.v3.Address;
import io.envoyproxy.envoy.config.core.v3.SocketAddress;
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment;
import io.envoyproxy.envoy.config.endpoint.v3.Endpoint;
import io.envoyproxy.envoy.config.endpoint.v3.LbEndpoint;
import io.envoyproxy.envoy.config.endpoint.v3.LocalityLbEndpoints;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.endpoint.v3.EndpointDiscoveryServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 描述：envoy的EndpointDS
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 19:19
 */
@Slf4j
@GrpcService
public class EdsGrpc extends EndpointDiscoveryServiceGrpc.EndpointDiscoveryServiceImplBase {

    /**
     * 描述：deltaEndpoints
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DeltaDiscoveryRequest> deltaEndpoints(StreamObserver<DeltaDiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DeltaDiscoveryRequest>() {
            @Override
            public void onNext(DeltaDiscoveryRequest deltaDiscoveryRequest) {
                log.trace("[envoy-endpoint]: deltaEndpoints\n[request: {}]",
                        deltaDiscoveryRequest);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-endpoint]: deltaEndpoints - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-endpoint]: deltaEndpoints - onCompleted");
            }
        };
    }

    /**
     * 描述：streamEndpoints
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public StreamObserver<DiscoveryRequest> streamEndpoints(StreamObserver<DiscoveryResponse> responseStreamObserver) {
        return new StreamObserver<DiscoveryRequest>() {
            @Override
            public void onNext(DiscoveryRequest discoveryRequest) {
                DiscoveryResponse discoveryResponse;
                try {
                    discoveryResponse = EdsGrpc.registerEndpoint();
                } catch (UnknownHostException e) {
                    log.error("[envoy-endpoint]: streamEndpoints \n [error: current get server ip error]");
                    throw new GrpcException(ErrorCodeMsg.ENVOY_ERROR);
                }
                log.trace("[envoy-endpoint]: streamEndpoints\n[request: {}], \n[response: {}]",
                        discoveryRequest, discoveryResponse);
                // return envoy
                responseStreamObserver.onNext(discoveryResponse);
            }

            @Override
            public void onError(Throwable t) {
                log.error("[envoy-endpoint]: streamEndpoints - [error: {}]", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.trace("[envoy-endpoint]: streamEndpoints - onCompleted");
            }
        };
    }

    /**
     * 描述：fetchEndpoints
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    @Override
    public void fetchEndpoints(DiscoveryRequest discoveryRequest, StreamObserver<DiscoveryResponse> responseObserver) {
        DiscoveryResponse discoveryResponse;
        try {
            discoveryResponse = EdsGrpc.registerEndpoint();
        } catch (UnknownHostException e) {
            log.error("[envoy-endpoint]: streamEndpoints \n [error: current get server ip error]");
            throw new GrpcException(ErrorCodeMsg.ENVOY_ERROR);
        }
        log.trace("[envoy-endpoint]: fetchEndpoints\n[request: {}], \n[response: {}]",
                discoveryRequest, discoveryResponse);
        responseObserver.onNext(discoveryResponse);
        responseObserver.onCompleted();
    }

    /**
     * 描述：return current server endpoint info
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 16:36
     */
    private static DiscoveryResponse registerEndpoint() throws UnknownHostException {
        // endpoint
        ClusterLoadAssignment clusterLoadAssignment = ClusterLoadAssignment.newBuilder()
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
                                                        .setAddress(EnvoyConstants.DEPLOY_TYPE_CONCENTRATED.equals(GrpcServiceConfig.getDeployType())
                                                                ? ListenerConstants.LOCALHOST : InetAddress.getLocalHost().getHostAddress())
                                                        // port
                                                        .setPortValue(GrpcServiceConfig.getGrpcServerPort())
                                                        // protocol
                                                        .setProtocolValue(SocketAddress.Protocol.TCP_VALUE)
                                                )
                                        )
                                )
                        )
                )
                .build();
        // return envoy register endpoint
        return DiscoveryResponse.newBuilder()
                .setTypeUrl(Resources.V3.ENDPOINT_TYPE_URL)
                .setVersionInfo(EnvoyConstants.CONFIGURATION_VERSION)
                .addResources(Any.pack(clusterLoadAssignment))
                .build();
    }

}
