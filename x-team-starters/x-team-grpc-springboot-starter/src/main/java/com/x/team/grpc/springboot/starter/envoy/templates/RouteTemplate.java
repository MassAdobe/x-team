package com.x.team.grpc.springboot.starter.envoy.templates;

import com.google.protobuf.Any;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.util.Durations;
import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.common.constants.HttpConstants;
import com.x.team.grpc.springboot.starter.config.GrpcServiceConfig;
import com.x.team.grpc.springboot.starter.config.WebExcludeConfig;
import com.x.team.grpc.springboot.starter.dto.WebExcludeDto;
import com.x.team.grpc.springboot.starter.envoy.constants.ClusterConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.RouterConstants;
import com.x.team.grpc.springboot.starter.envoy.v3.Resources;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.config.core.v3.RuntimeFractionalPercent;
import io.envoyproxy.envoy.config.route.v3.DirectResponseAction;
import io.envoyproxy.envoy.config.route.v3.HeaderMatcher;
import io.envoyproxy.envoy.config.route.v3.Route;
import io.envoyproxy.envoy.config.route.v3.RouteAction;
import io.envoyproxy.envoy.config.route.v3.RouteMatch;
import io.envoyproxy.envoy.extensions.filters.http.local_ratelimit.v3.LocalRateLimit;
import io.envoyproxy.envoy.type.v3.FractionalPercent;
import io.envoyproxy.envoy.type.v3.TokenBucket;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：ROUTE定义模板
 *
 * @author MassAdobe
 * @date Created in 2023/2/16 19:39
 */
public class RouteTemplate {

    /**
     * 集群名称
     */
    private final static String CLUSTER_NAME;

    // 初始化赋值
    static {
        // 集群名称
        CLUSTER_NAME = GrpcServiceConfig.getApplicationName()
                + ClusterConstants.CLUSTER_SUFFIX;
    }

    /**
     * 描述：生成动态路由配置
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 17:37
     */
    @SuppressWarnings("all")
    public static List<Route> generateRoutes() {
        // 获取 NACOS 中配置
        Map<String, WebExcludeDto> excludeUrls = WebExcludeConfig.getExcludeUrls();
        // 初始化
        final List<Route> routeList = new LinkedList<Route>();
        // 如果 NACOS 中配置不为空
        if (!excludeUrls.isEmpty()) {
            // 循环配置
            for (Map.Entry<String, WebExcludeDto> entry : excludeUrls.entrySet()) {
                // 路由匹配
                RouteMatch routeMatch = RouteMatch.newBuilder()
                        // 匹配路径
                        .setPrefix(CommonConstants.SLASH_MARK + entry.getValue().getUrl())
                        .build();
                // 路由动作
                RouteAction routeAction = RouteAction.newBuilder()
                        // 集群名称
                        .setCluster(RouteTemplate.CLUSTER_NAME)
                        // 默认3分钟超时
                        .setTimeout(Durations
                                .fromMinutes(RouterConstants.ROUTE_TIMEOUT_MINUTE))
                        .build();
                // 最大令牌数为${max_tokens} 每过${fill_interval}时间 向令牌桶中添加${tokens_per_fill}个令牌
                TokenBucket tokenBucket = TokenBucket.newBuilder()
                        .setMaxTokens(Objects.isNull(entry.getValue().getRateLimit())
                                ? GrpcServiceConfig.getMaxTokens() : entry.getValue().getRateLimit().getMaxTokens())
                        .setTokensPerFill(UInt32Value.of(Objects.isNull(entry.getValue().getRateLimit())
                                ? GrpcServiceConfig.getTokensPerFill() : entry.getValue().getRateLimit().getTokensPerFill()))
                        .setFillInterval(Durations.fromSeconds(Objects.isNull(entry.getValue().getRateLimit())
                                ? GrpcServiceConfig.getFillInterval() : entry.getValue().getRateLimit().getFillInterval()))
                        .build();
                // 控制是否启用
                RuntimeFractionalPercent filterEnabled = RuntimeFractionalPercent.newBuilder()
                        .setDefaultValue(FractionalPercent.newBuilder()
                                .setNumerator(100)
                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED))
                        .build();
                // 强制执行某一部分请求
                RuntimeFractionalPercent filterEnforced = RuntimeFractionalPercent.newBuilder()
                        .setDefaultValue(FractionalPercent.newBuilder()
                                .setNumerator(100)
                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED))
                        .build();
                // 如果被限流 添加头信息 x-rate-limit: on
                HeaderValueOption headerValueOption = HeaderValueOption.newBuilder()
                        .setHeader(HeaderValue.newBuilder()
                                .setKey(RouterConstants.RATE_LIMIT_HEADER_KEY)
                                .setValue(RouterConstants.RATE_LIMIT_HEADER_VALUE))
                        .build();
                // 组装速率限制
                LocalRateLimit localRateLimit;
                // 判断当前token bucket是否为空
                localRateLimit = LocalRateLimit.newBuilder()
                        // 前缀名称
                        .setStatPrefix(GrpcServiceConfig.getApplicationName()
                                + CommonConstants.HORIZONTAL_MARK
                                + entry.getKey()
                                + RouterConstants.ROUTE_REQUEST_LOCAL_LIMIT_SUFFIX)
                        // 最大令牌数为${max_tokens} 每过${fill_interval}时间 向令牌桶中添加${tokens_per_fill}个令牌
                        .setTokenBucket(tokenBucket)
                        // 控制是否启用
                        .setFilterEnabled(filterEnabled)
                        // 强制执行某一部分请求
                        .setFilterEnforced(filterEnforced)
                        // 如果被限流 添加头信息 x-rate-limit: on
                        .addResponseHeadersToAdd(headerValueOption)
                        .build();
                // 组装路由
                routeList.add(Route.newBuilder()
                        // 匹配
                        .setMatch(routeMatch)
                        // 路由动作
                        .setRoute(routeAction)
                        // 速率限制
                        .putAllTypedPerFilterConfig(Map.of(
                                // 解释：https://academy.tetrate.io/courses/take/envoy-fundamentals-zh/lessons/32007784-2-13
                                Resources.FILTER_ENVOY_LOCAL_RATELIMIT,
                                Any.pack(localRateLimit)))
                        .build());
            }
        }
        // 添加健康检查路由
        routeList.add(RouteTemplate.generateHealthRoute());
        // 添加"Authorization"全局路由
        routeList.add(RouteTemplate.generateAuthorizationRoute());
        // 添加默认全局路由
        routeList.add(RouteTemplate.generateDefaultRoute());
        return routeList;
    }

    /**
     * 描述：配置健康检查
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 18:09
     */
    @SuppressWarnings("all")
    private static Route generateHealthRoute() {
        return Route.newBuilder()
                // 匹配路径
                .setMatch(RouteMatch.newBuilder()
                        // 匹配 /health/check
                        .setPrefix(CommonConstants.SLASH_MARK + RouterConstants.MATCH_HEALTH_PREFIX))
                // 路由动作
                .setRoute(RouteAction.newBuilder()
                        // 集群名称
                        .setCluster(RouteTemplate.CLUSTER_NAME)
                        // 默认3分钟超时
                        .setTimeout(Durations.fromMinutes(RouterConstants.ROUTE_TIMEOUT_MINUTE)))
                // 速率限制
                .putAllTypedPerFilterConfig(Map.of(
                        // 解释：https://academy.tetrate.io/courses/take/envoy-fundamentals-zh/lessons/32007784-2-13
                        Resources.FILTER_ENVOY_LOCAL_RATELIMIT,
                        Any.pack(LocalRateLimit.newBuilder()
                                // 前缀名称
                                .setStatPrefix(GrpcServiceConfig.getApplicationName()
                                        + RouterConstants.ROUTE_REQUEST_HEALTH_LIMIT_SUFFIX
                                        + RouterConstants.ROUTE_REQUEST_LOCAL_LIMIT_SUFFIX)
                                // 健康检查 每5秒 允许获取一次
                                .setTokenBucket(TokenBucket.newBuilder()
                                        .setMaxTokens(1)
                                        .setTokensPerFill(UInt32Value.of(1))
                                        .setFillInterval(Durations.fromSeconds(5)))
                                // 控制是否启用
                                .setFilterEnabled(RuntimeFractionalPercent.newBuilder()
                                        .setDefaultValue(FractionalPercent.newBuilder()
                                                .setNumerator(100)
                                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED)))
                                // 强制执行某一部分请求
                                .setFilterEnforced(RuntimeFractionalPercent.newBuilder()
                                        .setDefaultValue(FractionalPercent.newBuilder()
                                                .setNumerator(100)
                                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED)))
                                // 如果被限流 添加头信息 x-rate-limit: on
                                .addResponseHeadersToAdd(HeaderValueOption.newBuilder()
                                        .setHeader(HeaderValue.newBuilder()
                                                .setKey(RouterConstants.RATE_LIMIT_HEADER_KEY)
                                                .setValue(RouterConstants.RATE_LIMIT_HEADER_VALUE)))
                                .build())))
                .build();
    }

    /**
     * 描述：配置Authorization全局路由
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 18:09
     */
    @SuppressWarnings("all")
    private static Route generateAuthorizationRoute() {
        return Route.newBuilder()
                // 匹配全局 “/”
                .setMatch(RouteMatch.newBuilder()
                        // 匹配路径 /${application_name}
                        .setPrefix(CommonConstants.SLASH_MARK + GrpcServiceConfig.getGrpcApplicationName())
                        // 添加头过滤
                        .addHeaders(
                                HeaderMatcher.newBuilder()
                                        // 需要“Authorization”的头
                                        .setName(GrpcConstants.AUTHORIZATION)
                                        // 当不存在“Authorization”的头时触发
                                        .setPresentMatch(Boolean.FALSE)))
                // 重定向返回值
                .setDirectResponse(DirectResponseAction.newBuilder()
                        // 设置 HTTP CODE 401
                        .setStatus(HttpConstants.HTTP_UNAUTHORIZED))
                .build();
    }

    /**
     * 描述：配置默认全局路由
     *
     * @author MassAdobe
     * @date Created in 2023/2/16 18:09
     */
    @SuppressWarnings("all")
    private static Route generateDefaultRoute() {
        return Route.newBuilder()
                // 匹配全局 “/”
                .setMatch(RouteMatch.newBuilder()
                        // 匹配路径 /${application_name}
                        .setPrefix(CommonConstants.SLASH_MARK + GrpcServiceConfig.getGrpcApplicationName())
                        // 添加头过滤
                        .addHeaders(
                                HeaderMatcher.newBuilder()
                                        // 需要“Authorization”的头
                                        .setName(GrpcConstants.AUTHORIZATION)
                                        // 当不存在“Authorization”的头时触发
                                        .setPresentMatch(Boolean.TRUE)
                        )
                )
                // 路由动作
                .setRoute(RouteAction.newBuilder()
                        // 集群名称
                        .setCluster(RouteTemplate.CLUSTER_NAME)
                        // 默认3分钟超时
                        .setTimeout(Durations.fromMinutes(RouterConstants.ROUTE_TIMEOUT_MINUTE)))
                // 速率限制
                .putAllTypedPerFilterConfig(Map.of(
                        // 解释：https://academy.tetrate.io/courses/take/envoy-fundamentals-zh/lessons/32007784-2-13
                        Resources.FILTER_ENVOY_LOCAL_RATELIMIT,
                        Any.pack(LocalRateLimit.newBuilder()
                                // 前缀名称
                                .setStatPrefix(GrpcServiceConfig.getApplicationName()
                                        + RouterConstants.ROUTE_REQUEST_LOCAL_LIMIT_SUFFIX)
                                // 健康检查 每5秒 允许获取一次
                                .setTokenBucket(TokenBucket.newBuilder()
                                        .setMaxTokens(GrpcServiceConfig.getMaxTokens())
                                        .setTokensPerFill(UInt32Value.of(GrpcServiceConfig.getTokensPerFill()))
                                        .setFillInterval(Durations.fromSeconds(GrpcServiceConfig.getFillInterval())))
                                // 控制是否启用
                                .setFilterEnabled(RuntimeFractionalPercent.newBuilder()
                                        .setDefaultValue(FractionalPercent.newBuilder()
                                                .setNumerator(100)
                                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED)))
                                // 强制执行某一部分请求
                                .setFilterEnforced(RuntimeFractionalPercent.newBuilder()
                                        .setDefaultValue(FractionalPercent.newBuilder()
                                                .setNumerator(100)
                                                .setDenominator(FractionalPercent.DenominatorType.HUNDRED)))
                                // 如果被限流 添加头信息 x-rate-limit: on
                                .addResponseHeadersToAdd(HeaderValueOption.newBuilder()
                                        .setHeader(HeaderValue.newBuilder()
                                                .setKey(RouterConstants.RATE_LIMIT_HEADER_KEY)
                                                .setValue(RouterConstants.RATE_LIMIT_HEADER_VALUE)))
                                .build())))
                .build();
    }
}
