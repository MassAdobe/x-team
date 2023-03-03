package com.x.team.grpc.springboot.starter.config;

import com.x.team.common.constants.CommonConstants;
import com.x.team.config.springboot.starter.utils.XTeamSpringUtils;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcServiceDefinition;
import net.devh.boot.grpc.server.service.GrpcServiceDiscoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：GRPC 服务配置
 *
 * @author 月关
 * @date Created in 2023/2/13 10:00
 */
@Slf4j
@Resource
@Component
@RefreshScope
public class GrpcServiceConfig {

    /**
     * application name
     */
    private static String applicationName;

    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        GrpcServiceConfig.applicationName = applicationName;
        GrpcServiceConfig.setGrpcApplicationName(
                applicationName.replaceAll(CommonConstants.HORIZONTAL_MARK, CommonConstants.POT_MARK)
                        + EnvoyConstants.GRPC_SERVICE_SUFFIX);
    }

    public static String getApplicationName() {
        return GrpcServiceConfig.applicationName;
    }

    /**
     * gRPC application name
     */
    private static String grpcApplicationName;

    public static String getGrpcApplicationName() {
        return grpcApplicationName;
    }

    public static void setGrpcApplicationName(String grpcApplicationName) {
        GrpcServiceConfig.grpcApplicationName = grpcApplicationName;
    }

    /**
     * application gRPC port
     */
    private static Integer grpcServerPort;

    @Value("${grpc.server.port:9090}")
    public void setGrpcServerPort(Integer grpcServerPort) {
        GrpcServiceConfig.grpcServerPort = grpcServerPort;
    }

    public static Integer getGrpcServerPort() {
        return GrpcServiceConfig.grpcServerPort;
    }

    /**
     * 当前环境
     */
    public static String environment;

    @Value("${spring.profiles.active:native}")
    public void setEnvironment(String environment) {
        GrpcServiceConfig.environment = environment;
    }

    public static String getEnvironment() {
        return GrpcServiceConfig.environment;
    }

    /**
     * rate limit 最大令牌数
     */
    private static Integer maxTokens;

    @Value("${web.rate-limit.max-tokens:5678}")
    public void setMaxTokens(Integer maxTokens) {
        GrpcServiceConfig.maxTokens = maxTokens;
    }

    public static Integer getMaxTokens() {
        return GrpcServiceConfig.maxTokens;
    }

    /**
     * rate limit 填充时间(秒)
     */
    private static Long fillInterval;

    @Value("${web.rate-limit.fill-interval:1}")
    public void setFillInterval(Long fillInterval) {
        GrpcServiceConfig.fillInterval = fillInterval;
    }

    public static Long getFillInterval() {
        return GrpcServiceConfig.fillInterval;
    }

    /**
     * rate limit 填充时间内添加的令牌数
     */
    private static Integer tokensPerFill;

    @Value("${web.rate-limit.tokens-per-fill:4567}")
    public void setTokensPerFill(Integer tokensPerFill) {
        GrpcServiceConfig.tokensPerFill = tokensPerFill;
    }

    public static Integer getTokensPerFill() {
        return GrpcServiceConfig.tokensPerFill;
    }

    /**
     * 部署方式：分裂/集中
     */
    private static String deployType;

    @Value("${deploy-type:concentrated}")
    public void setDeployType(String deployType) {
        GrpcServiceConfig.deployType = deployType;
    }

    public static String getDeployType() {
        return GrpcServiceConfig.deployType;
    }

    /**
     * gRPC服务列表
     */
    public static List<String> grpcServiceList;

    /**
     * 扫描gRPC服务
     */
    @Bean
    public List<String> loadGrpcService() {
        GrpcServiceDiscoverer grpcServiceDiscoverer = XTeamSpringUtils.getBean(GrpcServiceDiscoverer.class);
        Collection<GrpcServiceDefinition> grpcServices = grpcServiceDiscoverer.findGrpcServices();
        if (!CollectionUtils.isEmpty(grpcServices)) {
            GrpcServiceConfig.grpcServiceList = new ArrayList<>(grpcServices.size());
            GrpcServiceConfig.grpcServiceList.addAll(grpcServices.stream()
                    .map(grpcServiceDefinition -> grpcServiceDefinition.getDefinition().getServiceDescriptor().getName())
                    .collect(Collectors.toList()));
            return GrpcServiceConfig.grpcServiceList;
        }
        return null;
    }
}
