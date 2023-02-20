package com.x.team.grpc.springboot.starter.envoy.constants;

/**
 * 描述：envoy常量池
 *
 * @author 月关
 * @date Created in 2023/2/10 19:56
 */
public class EnvoyConstants {
    /**
     * 配置版本
     */
    public static final String CONFIGURATION_VERSION = "0";
    /**
     * xds服务名称尾缀
     */
    public static final String XDS_SERVER_SUFFIX = "-xds";
    /**
     * gRPC的PB文件路径
     */
    public static final String GRPC_PB_PATH = "/etc/envoy/pb/";
    /**
     * PB文件尾缀
     */
    public static final String GRPC_PB_FILE_SUFFIX = ".pb";
    /**
     * DOCKER-IP: host.docker.internal
     */
    public static final String DOCKER_ADDRESS = "host.docker.internal";
    /**
     * gRPC service suffix
     */
    public static final String GRPC_SERVICE_SUFFIX = ".service";
}
