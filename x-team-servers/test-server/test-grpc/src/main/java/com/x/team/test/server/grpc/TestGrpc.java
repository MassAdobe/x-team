package com.x.team.test.server.grpc;

import com.x.team.test.server.service.TestService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import test.server.message.TestRequest;
import test.server.message.TestResponse;
import test.server.service.TestApiGrpc;

/**
 * 描述：测试GRPC
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 14:22
 */
@Slf4j
@GrpcService
public class TestGrpc extends TestApiGrpc.TestApiImplBase {

    @Autowired
    private TestService testService;

    /**
     * 测试接口
     */
    @Override
    public void testing(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        responseStreamObserver.onNext(this.testService.testing(testRequest));
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Get
     */
    @Override
    public void testGet(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        responseStreamObserver.onNext(this.testService.proxyGet(testRequest));
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Post
     */
    @Override
    public void testPost(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        responseStreamObserver.onNext(this.testService.testing(testRequest));
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Put
     */
    @Override
    public void testPut(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        responseStreamObserver.onNext(this.testService.testing(testRequest));
        responseStreamObserver.onCompleted();
    }

    /**
     * 测试Proxy-Delete
     */
    @Override
    public void testDelete(TestRequest testRequest, StreamObserver<TestResponse> responseStreamObserver) {
        responseStreamObserver.onNext(this.testService.testing(testRequest));
        responseStreamObserver.onCompleted();
    }
}
