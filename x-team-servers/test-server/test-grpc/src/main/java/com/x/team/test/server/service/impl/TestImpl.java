package com.x.team.test.server.service.impl;

import com.x.team.common.constants.CloudServerConstants;
import com.x.team.common.constants.SqlConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.utils.CommonUtils;
import com.x.team.test.server.entity.TUser;
import com.x.team.test.server.repository.TUserMapper;
import com.x.team.test.server.service.TestService;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.server.message.TestRequest;
import test.server.message.TestResponse;
import test.server.service.TestApiGrpc;
import tk.mybatis.mapper.entity.Example;

import java.util.Objects;

/**
 * 描述：测试接口实现类
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 14:25
 */
@Slf4j
@Service
public class TestImpl implements TestService {

    @GrpcClient(CloudServerConstants.CLIENT_TEST_SERVER)
    private TestApiGrpc.TestApiBlockingStub testApiGrpc;

    @Autowired
    private TUserMapper tUserMapper;

    /**
     * 测试接口
     */
    @Override
    public TestResponse testing(TestRequest testRequest) {
//        int x = 0 / 0;
//        throw new GrpcException(ErrorCodeMsg.PARAM_ERROR);
        UserContext userContext = HeaderContext.getContext();
        // 日志
        log.info("[api-testing]: header-info: {}", userContext.toString());
        // 循环调用
        TestResponse testResponse = this.testApiGrpc.testGet(TestRequest.newBuilder()
                .setFirstId("inner call first")
                .build());
        log.info("[inner-call]: response: {}", testResponse);
        // mysql8 调用
        Example example = new Example(TUser.class);
        example.createCriteria()
                .andEqualTo("id", testRequest.getFirstId())
                .andEqualTo("isDeleted", SqlConstants.UNDELETED);
        TUser tUser = this.tUserMapper.selectOneByExample(example);
        if (Objects.nonNull(tUser)) {
            return TestResponse.newBuilder()
                    .setA(tUser.getRealName())
                    .setB(CommonUtils.getRandomNumBetween(0, 9999))
                    .build();
        }
        return TestResponse.newBuilder().build();
    }

    /**
     * 测试内部调用
     */
    @Override
    public TestResponse proxyGet(TestRequest testRequest) {
        log.info("[inner-call-twice]: request: {}", testRequest);
        return TestResponse.newBuilder()
                .setA("AAA")
                .setB(9999999)
                .build();
    }
}
