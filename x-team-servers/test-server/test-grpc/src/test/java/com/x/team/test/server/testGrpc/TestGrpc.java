package com.x.team.test.server.testGrpc;

import com.x.team.common.constants.CommonConstants;
import com.x.team.grpc.springboot.starter.envoy.constants.EnvoyConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 描述：Test TestGrpc
 *
 * @author MassAdobe
 * @date Created in 2023/2/17 17:10
 */
@Slf4j
@SpringBootTest
public class TestGrpc {

    /**
     * 描述：自行研究
     *
     * @author MassAdobe
     * @date Created in 2023/2/17 17:25
     */
    @Test
    public void testCommon() {
        String str = "test-server";
        String newStr = str.replaceAll(CommonConstants.HORIZONTAL_MARK, CommonConstants.POT_MARK)
                + EnvoyConstants.GRPC_SERVICE_SUFFIX;
        log.info("[test-common]: {}", newStr);
    }
}
