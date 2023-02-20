package com.x.team.test.schedule.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.x.team.common.constants.CloudServerConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.common.utils.JsonUtils;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import com.x.team.schedule.springboot.starter.annotation.Schedule;
import com.x.team.schedule.springboot.starter.constants.ExecutorConstants;
import com.x.team.schedule.springboot.starter.exception.ScheduleException;
import com.x.team.schedule.springboot.starter.utils.TransportUtils;
import com.x.team.test.schedule.vo.useApi.TestRequestVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import test.server.message.TestRequest;
import test.server.message.TestResponse;
import test.server.service.UseApiGrpc;

/**
 * 描述：调度Use方法
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 11:37
 */
@Slf4j
@Component
public class UseExecutor {

    /**
     * 引用gRPC服务
     */
    @GrpcClient(CloudServerConstants.CLIENT_TEST_SERVER)
    private UseApiGrpc.UseApiBlockingStub useApiBlockingStub;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @Schedule
    @XxlJob(ExecutorConstants.USE_API_EXECUTOR)
    public ReturnT<String> userApiExecutor() {
        // 获取参数
        String jobParam = XxlJobHelper.getJobParam();
        // 判断参数是否为空
        if (!StringUtils.isEmpty(jobParam)) {
            // 打印日志
            log.info("[{}]: request-param: {}", ExecutorConstants.USE_API_EXECUTOR, jobParam);
            try {
                // marshalling request vo
                TestRequestVo testRequestVo = JsonUtils.OBJECT_MAPPER.readValue(jobParam, TestRequestVo.class);
                // transport request vo to proto vo
                TestRequest.Builder testRequestBuilder = TransportUtils.toProto(TestRequest.newBuilder(), testRequestVo);
                // calling gRPC service
                TestResponse testResponse = this.useApiBlockingStub.apiTest(testRequestBuilder.build());
                // print log to xxl-job-admin
                XxlJobHelper.handleSuccess(String.format("[{%s}]: response-param: [traceId: %s], [stepId: %s], [param: %s]",
                        ExecutorConstants.USE_API_EXECUTOR,
                        MDC.get(TraceConstants.TRACE_ID),
                        MDC.get(TraceConstants.STEP_ID),
                        testResponse.toString()));
            } catch (JsonProcessingException e) {
                log.error("[{}]: json format error, error: {}", ExecutorConstants.USE_API_EXECUTOR, e.getMessage());
                throw new ScheduleException(ErrorCodeMsg.JSON_ERROR);
            }
        }
        // return success to xxl-job-admin
        return ReturnT.success();
    }
}
