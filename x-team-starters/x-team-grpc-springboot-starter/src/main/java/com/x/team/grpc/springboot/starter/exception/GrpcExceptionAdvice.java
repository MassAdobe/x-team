package com.x.team.grpc.springboot.starter.exception;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.x.team.common.constants.CloudServerConstants;
import com.x.team.common.constants.GrpcConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.rest.NoticeDto;
import com.x.team.common.enums.ErrorCodeMsg;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

/**
 * 描述：全局GRPC错误拦截器
 * <p/>
 * GRPC 错误传输只能使用英文
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:07
 */
@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {

    /**
     * 描述：handle catched global exceptions
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 16:13
     */
    @SuppressWarnings("all")
    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException errorHandler(Exception exception) {
        log.error("[grpc-error]: unknown-exception: ", exception);
        if (exception instanceof IllegalStateException) {
            return null;
        }
        NoticeDto noticeDto = ErrorCodeMsg.getError(
                HeaderContext.getContext().getRequestContext().getI18n(),
                ErrorCodeMsg.UNKNOWN_ERROR);
        return StatusProto.toStatusRuntimeException(com.google.rpc.Status.newBuilder()
                .setCode(Code.UNKNOWN.getNumber())
                .setMessage(noticeDto.getMsg())
                .addDetails(Any.pack(ErrorInfo.newBuilder()
                        .setDomain(CloudServerConstants.DOMAIN)
                        .setReason(GrpcConstants.GRPC_RETURN_UNKNOWN_ERROR)
                        .putMetadata(GrpcConstants.ERROR_STATUS, noticeDto.getStatus())
                        .putMetadata(GrpcConstants.ERROR_MESSAGE, noticeDto.getMsg())
                        .build()))
                .build());
    }

    /**
     * 描述：filter handle with self defined exceptions: StatusRuntimeException
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 16:13
     */
    @SuppressWarnings("all")
    @GrpcExceptionHandler(StatusRuntimeException.class)
    public StatusRuntimeException grpcErrorHandler(StatusRuntimeException statusRuntimeException) {
        log.error("[grpc-error]: transaction-exception: ", statusRuntimeException);
        return statusRuntimeException;
    }

    /**
     * 描述：filter handle with self defined exceptions: GrpcException
     *
     * @author MassAdobe
     * @date Created in 2023/1/29 16:13
     */
    @SuppressWarnings("all")
    @GrpcExceptionHandler(GrpcException.class)
    public StatusRuntimeException grpcErrorHandler(GrpcException grpcException) {
        log.error("[grpc-error]: service-exception: ", grpcException.getErrorCodeMsg());
        NoticeDto noticeDto = ErrorCodeMsg.getError(
                HeaderContext.getContext().getRequestContext().getI18n(),
                grpcException.getErrorCodeMsg());
        return StatusProto.toStatusRuntimeException(com.google.rpc.Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT.getNumber())
                .setMessage(noticeDto.getMsg())
                .addDetails(Any.pack(ErrorInfo.newBuilder()
                        .setDomain(CloudServerConstants.DOMAIN)
                        .setReason(GrpcConstants.GRPC_RETURN_SERVICE_ERROR)
                        .putMetadata(GrpcConstants.ERROR_STATUS, noticeDto.getStatus())
                        .putMetadata(GrpcConstants.ERROR_MESSAGE, noticeDto.getMsg())
                        .build()))
                .build());
    }
}