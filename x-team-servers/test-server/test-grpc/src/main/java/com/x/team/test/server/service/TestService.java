package com.x.team.test.server.service;

import test.server.message.TestRequest;
import test.server.message.TestResponse;

/**
 * 描述：测试接口类
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 14:24
 */
public interface TestService {

    /**
     * 测试接口
     */
    TestResponse testing(TestRequest testRequest);

    /**
     * 测试内部调用
     */
    TestResponse proxyGet(TestRequest testRequest);
}
