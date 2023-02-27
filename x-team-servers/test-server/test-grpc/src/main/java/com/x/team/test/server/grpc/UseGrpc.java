package com.x.team.test.server.grpc;

import com.google.protobuf.Empty;
import com.x.team.common.utils.CommonUtils;
import com.x.team.test.server.service.TestService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import test.server.message.TestRequest;
import test.server.message.TestResponse;
import test.server.service.UseApiGrpc;

/**
 * 描述：测试envoy连接转换
 *
 * @author MassAdobe
 * @date Created in 2023/2/6 10:05
 */
@Slf4j
@GrpcService
public class UseGrpc extends UseApiGrpc.UseApiImplBase {

    @Autowired
    private TestService testService;

    /**
     * 测试接口
     */
    @Override
    public void apiTest(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        log.info("[测试接口]: apiTest: {}", testRequest);
        TestResponse testing = this.testService.testing(testRequest);
        log.info("[测试接口]: inner-call-response: {}", testing);
//        int x = 1 / 0;
//        throw new GrpcException(ErrorCodeMsg.SERVER_ERROR);
        responseStreamObserver.onNext(TestResponse.newBuilder()
                .setA("apiTest-" + CommonUtils.getRandomString(4))
                .setB(CommonUtils.getRandomNumBetween(0, 9999))
                .build());
        responseStreamObserver.onCompleted();
//
//        var status = Status.INVALID_ARGUMENT.withDescription("abc").withCause(new RuntimeException("123"));
//        responseStreamObserver.onError(status.asException());
    }

    /**
     * 测试Proxy-Get
     */
    @Override
    public void apiGet(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        log.info("[测试接口]: apiGet: {}", testRequest);
        responseStreamObserver.onNext(TestResponse.newBuilder()
                .setA("apiGet-" + CommonUtils.getRandomString(4))
                .setB(CommonUtils.getRandomNumBetween(0, 9999))
                .build());
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Get
     */
    @Override
    public void apiGetOther(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        log.info("[测试接口]: apiGetOther: {}", testRequest);
        responseStreamObserver.onNext(TestResponse.newBuilder()
                .setA("apiGetOther-" + CommonUtils.getRandomString(4))
                .setB(CommonUtils.getRandomNumBetween(0, 9999))
                .build());
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Post
     */
    @Override
    public void apiPost(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        log.info("[测试接口]: apiPost: {}", testRequest);
        responseStreamObserver.onNext(TestResponse.newBuilder()
                .setA("apiPost-" + CommonUtils.getRandomString(4))
                .setB(CommonUtils.getRandomNumBetween(0, 9999))
                .build());
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Put
     */
    @Override
    public void apiPut(TestRequest testRequest, StreamObserver<Empty> responseStreamObserver) {
        log.info("[测试接口]: apiPut: {}", testRequest);
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Delete
     */
    @Override
    public void apiDelete(TestRequest testRequest, StreamObserver<Empty> responseStreamObserver) {
        log.info("[测试接口]: apiDelete: {}", testRequest);
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试自定义
     */
    @Override
    public void apiSelfPost(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        log.info("[测试接口]: apiSelfPost: {}", testRequest);
        responseStreamObserver.onNext(TestResponse.newBuilder()
                .setA("apiSelfPost-" + CommonUtils.getRandomString(4))
                .setB(CommonUtils.getRandomNumBetween(0, 9999))
                .build());
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试自定义
     */
    @Override
    public void apiSelfPut(TestRequest testRequest, StreamObserver<Empty> responseStreamObserver) {
        log.info("[测试接口]: apiSelfPost: {}", testRequest);
        responseStreamObserver.onCompleted();
    }
}
