package com.x.team.rest.springboot.starter.exception;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.ErrorInfo;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.rest.springboot.starter.dto.Resp;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
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
public class RestExceptionAdvice {

    /**
     * 描述：handle catched global exceptions
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 14:07
     */
    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public Resp errorHandler(Exception exception) {
        log.error("[rest-error]: system-exception", exception);
        if (exception instanceof StatusRuntimeException) {
            System.out.println();
        }
        return Resp.failure(ErrorCodeMsg.UNKNOWN_ERROR);
    }

    /**
     * 描述：filter handle with self defined rest exceptions: RestException
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 14:10
     */
    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = RestException.class)
    public Resp restErrorHandler(RestException restException) {
        log.info("[rest-error]: service-exception: {} - {} - {}",
                restException.getErrorCodeMsg().getStatus(),
                restException.getErrorCodeMsg().getZhMessage(),
                restException.getErrorCodeMsg().getEnMessage());
        return Resp.failure(restException.getErrorCodeMsg());
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
    public Resp grpcErrorHandler(StatusRuntimeException statusRuntimeException) {
        com.google.rpc.Status rpcStatus = StatusProto.fromThrowable(statusRuntimeException);
        try {
            ErrorInfo errorInfo = rpcStatus.getDetails(0).unpack(ErrorInfo.class);
            String status = errorInfo.getMetadataMap().get(GrpcConstants.ERROR_STATUS);
            ErrorCodeMsg errorCodeMsg = ErrorCodeMsg.getValue(status);
            log.info("[rest-error]: grpc-service-exception: {} - {} - {}",
                    errorCodeMsg.getStatus(),
                    errorCodeMsg.getZhMessage(),
                    errorCodeMsg.getEnMessage());
            return Resp.failure(errorCodeMsg);
        } catch (InvalidProtocolBufferException e) {
            log.error("[grpc-client]: [grpc-service-exception: {}]", e.getMessage());
            throw new RestException(ErrorCodeMsg.GRPC_ERROR);
        }
    }
}
