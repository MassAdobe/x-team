package com.x.team.schedule.springboot.starter.exception;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.ErrorInfo;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.log.springboot.starter.constants.TraceConstants;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 描述：catch exceptions advice
 *
 * @author MassAdobe
 * @date Created in 2023/1/29 14:06
 */
@Slf4j
@RestControllerAdvice
public class ScheduleExceptionAdvice {

    /**
     * 描述：handle catched global exceptions
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 14:07
     */
    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public ReturnT<String> errorHandler(Exception exception) {
        log.error("[schedule-error]: system-exception", exception);
        XxlJobHelper.handleFail(String.format("[schedule-error]: system-exception: [traceId: %s], [stepId: %s], [error: %s]",
                MDC.get(TraceConstants.TRACE_ID), MDC.get(TraceConstants.STEP_ID), exception.getMessage()));
        return ReturnT.failure(exception.getMessage());
    }

    /**
     * 描述：filter handle with self defined rest exceptions: ScheduleException
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 14:10
     */
    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = ScheduleException.class)
    public ReturnT<String> restErrorHandler(ScheduleException scheduleException) {
        log.error("[schedule-error]: service-exception: \nstatus: {}, \nmessage: {}",
                scheduleException.getErrorCodeMsg().getStatus(),
                scheduleException.getErrorCodeMsg().getEnMessage());
        XxlJobHelper.handleFail(String.format("[schedule-error]: service-exception: [traceId: %s], [stepId: %s], [error: %s]",
                MDC.get(TraceConstants.TRACE_ID), MDC.get(TraceConstants.STEP_ID), scheduleException.getMessage()));
        return ReturnT.failure(scheduleException.getMessage());
    }

    /**
     * 描述：filter handle with self defined grpc exceptions: StatusRuntimeException
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 14:07
     */
    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = StatusRuntimeException.class)
    public ReturnT<String> grpcErrorHandler(StatusRuntimeException statusRuntimeException) {
        com.google.rpc.Status rpcStatus = StatusProto.fromThrowable(statusRuntimeException);
        try {
            ErrorInfo errorInfo = rpcStatus.getDetails(0).unpack(ErrorInfo.class);
            String status = errorInfo.getMetadataMap().get(GrpcConstants.ERROR_STATUS);
            ErrorCodeMsg errorCodeMsg = ErrorCodeMsg.getValue(status);
            log.error("[schedule-error]: grpc-service-exception: \nstatus: {}, \nmessage: {}",
                    errorCodeMsg.getStatus(),
                    errorCodeMsg.getEnMessage());
            XxlJobHelper.handleFail(String.format("[schedule-error]: grpc-service-exception: [traceId: %s], [stepId: %s], [error: %s]",
                    MDC.get(TraceConstants.TRACE_ID), MDC.get(TraceConstants.STEP_ID), errorCodeMsg.getEnMessage()));
            return ReturnT.failure(errorCodeMsg.getEnMessage());
        } catch (InvalidProtocolBufferException e) {
            log.error("[grpc-client]: [grpc-service-exception: {}]", e.getMessage());
            throw new ScheduleException(ErrorCodeMsg.GRPC_ERROR);
        }
    }
}
