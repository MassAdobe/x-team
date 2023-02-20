package com.x.team.grpc.springboot.starter.envoy.v3;

import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import io.envoyproxy.envoy.config.core.v3.ApiConfigSource;
import io.envoyproxy.envoy.config.core.v3.ApiVersion;
import io.envoyproxy.envoy.config.core.v3.ConfigSource;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：XDS服务
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 18:14
 */
@Slf4j
public class XdsGrpc {

    /**
     * 描述：返回RDS配置
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 18:15
     */
    public static ConfigSource rdsClusterConfiguration() {
        // 返回RDS配置
        return ConfigSource.newBuilder()
                .setResourceApiVersion(ApiVersion.V3)
                .setApiConfigSource(ApiConfigSource.newBuilder()
                        .setApiType(ApiConfigSource.ApiType.GRPC)
                        .setTransportApiVersion(ApiVersion.V3)
                        .addGrpcServices(io.envoyproxy.envoy.config.core.v3.GrpcService.newBuilder()
                                .setEnvoyGrpc(io.envoyproxy.envoy.config.core.v3.GrpcService.EnvoyGrpc.newBuilder()
                                        .setClusterName(GrpcServiceConfig.getApplicationName()
                                                + EnvoyConstants.XDS_SERVER_SUFFIX))))
                .build();
    }

    /**
     * 描述：返回EDS配置
     *
     * @author MassAdobe
     * @date Created in 2023/2/13 18:20
     */
    public static ConfigSource edsClusterConfiguration() {
        return ConfigSource.newBuilder()
                .setResourceApiVersion(ApiVersion.V3)
                .setApiConfigSource(ApiConfigSource.newBuilder()
                        .setApiType(ApiConfigSource.ApiType.GRPC)
                        .setTransportApiVersion(ApiVersion.V3)
                        .addGrpcServices(io.envoyproxy.envoy.config.core.v3.GrpcService.newBuilder()
                                .setEnvoyGrpc(io.envoyproxy.envoy.config.core.v3.GrpcService.EnvoyGrpc.newBuilder()
                                        .setClusterName(GrpcServiceConfig.getApplicationName()
                                                + EnvoyConstants.XDS_SERVER_SUFFIX))))
                .build();
    }
}
