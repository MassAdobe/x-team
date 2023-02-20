package com.x.team.grpc.springboot.starter.health;

import io.grpc.health.server.HealthCheckRequest;
import io.grpc.health.server.HealthCheckResponse;
import io.grpc.health.server.HealthGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 描述：健康检查(k8s)
 *
 * @author MassAdobe
 * @date Created in 2023/2/17 20:13
 */
@Slf4j
@GrpcService
public class HealthServerGrpc extends HealthGrpc.HealthImplBase {

    @Override
    public void check(HealthCheckRequest healthCheckRequest, StreamObserver<HealthCheckResponse> responseObserver) {
        log.trace("[health-server]: check");
        responseObserver.onNext(HealthCheckResponse.newBuilder()
                .setStatus(HealthCheckResponse.ServingStatus.SERVING)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void watch(HealthCheckRequest healthCheckRequest, StreamObserver<HealthCheckResponse> responseObserver) {
        log.trace("[health-server]: watch");
        responseObserver.onNext(HealthCheckResponse.newBuilder()
                .setStatus(HealthCheckResponse.ServingStatus.SERVING)
                .build());
        responseObserver.onCompleted();
    }
}
