package com.x.team.grpc.springboot.starter.envoy.v3;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment;
import io.envoyproxy.envoy.config.listener.v3.Listener;
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration;
import io.envoyproxy.envoy.extensions.transport_sockets.tls.v3.Secret;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 描述：envoy参数
 *
 * @author MassAdobe
 * @date Created in 2023/2/10 19:09
 */
@SuppressWarnings("all")
@AllArgsConstructor
public class Resources {

    /**
     * Version-agnostic representation of a resource. This is useful when the version qualifier isn't
     * needed.
     */
    public enum ResourceType {
        CLUSTER,
        ENDPOINT,
        LISTENER,
        ROUTE,
        SECRET
    }

    public enum ApiVersion {
        V3
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Resources.class);

    // HTTP FILTER
    public static final String FILTER_ENVOY_CORS = "envoy.filters.http.cors";
    public static final String FILTER_ENVOY_LOCAL_RATELIMIT = "envoy.filters.http.local_ratelimit";
    public static final String FILTER_ENVOY_GRPC_WEB = "envoy.filters.http.grpc_web";
    public static final String FILTER_ENVOY_GRPC_JSON_TRANSCODER = "envoy.filters.http.grpc_json_transcoder";
    public static final String FILTER_ENVOY_ROUTER = "envoy.filters.http.router";
    public static final String FILTER_HTTP_CONNECTION_MANAGER = "envoy.http_connection_manager";

    // UPSTREAM EXTENSIONS
    public static final String UPSTREAM_EXTENSION_HTTP_PROTOCOL_OPTIONS = "envoy.extensions.upstreams.http.v3.HttpProtocolOptions";

    public static class V3 {

        // 基本TYPE
        public static final String CLUSTER_TYPE_URL =
                "type.googleapis.com/envoy.config.cluster.v3" + ".Cluster";
        public static final String ENDPOINT_TYPE_URL =
                "type.googleapis.com/envoy.config.endpoint.v3" + ".ClusterLoadAssignment";
        public static final String LISTENER_TYPE_URL =
                "type.googleapis.com/envoy.config.listener.v3" + ".Listener";
        public static final String ROUTE_TYPE_URL =
                "type.googleapis.com/envoy.config.route.v3" + ".RouteConfiguration";
        public static final String SECRET_TYPE_URL =
                "type.googleapis.com/envoy.extensions" + ".transport_sockets.tls.v3.Secret";

        public static final List<String> TYPE_URLS =
                ImmutableList.of(
                        CLUSTER_TYPE_URL,
                        ENDPOINT_TYPE_URL,
                        LISTENER_TYPE_URL,
                        ROUTE_TYPE_URL,
                        SECRET_TYPE_URL);
    }

    public static final List<ResourceType> RESOURCE_TYPES_IN_ORDER =
            ImmutableList.of(ResourceType.CLUSTER, ResourceType.ENDPOINT, ResourceType.LISTENER, ResourceType.ROUTE, ResourceType.SECRET);

    public static final Map<String, ResourceType> TYPE_URLS_TO_RESOURCE_TYPE =
            new ImmutableMap.Builder<String, ResourceType>()
                    .put(V3.CLUSTER_TYPE_URL, ResourceType.CLUSTER)
                    .put(V3.ENDPOINT_TYPE_URL, ResourceType.ENDPOINT)
                    .put(V3.LISTENER_TYPE_URL, ResourceType.LISTENER)
                    .put(V3.ROUTE_TYPE_URL, ResourceType.ROUTE)
                    .put(V3.SECRET_TYPE_URL, ResourceType.SECRET)
                    .build();

    public static final Map<String, Class<? extends Message>> RESOURCE_TYPE_BY_URL =
            new ImmutableMap.Builder<String, Class<? extends Message>>()
                    .put(V3.CLUSTER_TYPE_URL, Cluster.class)
                    .put(V3.ENDPOINT_TYPE_URL, ClusterLoadAssignment.class)
                    .put(V3.LISTENER_TYPE_URL, Listener.class)
                    .put(V3.ROUTE_TYPE_URL, RouteConfiguration.class)
                    .put(V3.SECRET_TYPE_URL, Secret.class)
                    .build();

    /**
     * Returns the name of the given resource message.
     *
     * @param resource the resource message
     */
    public static String getResourceName(Message resource) {
        if (resource instanceof Cluster) {
            return ((Cluster) resource).getName();
        }

        if (resource instanceof ClusterLoadAssignment) {
            return ((ClusterLoadAssignment) resource).getClusterName();
        }

        if (resource instanceof Listener) {
            return ((Listener) resource).getName();
        }

        if (resource instanceof RouteConfiguration) {
            return ((RouteConfiguration) resource).getName();
        }

        if (resource instanceof Secret) {
            return ((Secret) resource).getName();
        }

        return "";
    }

    /**
     * Returns the name of the given resource message.
     *
     * @param anyResource the resource message
     * @throws RuntimeException if the passed Any doesn't correspond to an xDS resource
     */
    public static String getResourceName(Any anyResource) {
        Class<? extends Message> clazz = RESOURCE_TYPE_BY_URL.get(anyResource.getTypeUrl());
        Preconditions.checkNotNull(clazz, "cannot unpack non-xDS message type");

        try {
            return getResourceName(anyResource.unpack(clazz));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the supported API version for a given type URL.
     */
    public static ApiVersion getResourceApiVersion(String typeUrl) {
        if (V3.TYPE_URLS.contains(typeUrl)) {
            return ApiVersion.V3;
        }

        throw new RuntimeException(String.format("Unsupported API version for type URL %s", typeUrl));
    }
}
