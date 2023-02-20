package com.x.team.redis.springboot.starter.exception;

import com.x.team.common.enums.ErrorCodeMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：redis错误
 *
 * @author 月关
 * @date Created in 2022/1/14 6:52 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedisException extends RuntimeException {

    private ErrorCodeMsg errorCodeMsg;
    private String status;
    private String message;

    public RedisException(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public RedisException(ErrorCodeMsg errorCodeMsg) {
        this.errorCodeMsg = errorCodeMsg;
        this.status = errorCodeMsg.getStatus();
        this.message = errorCodeMsg.getZhMessage();
    }

    public RedisException(ErrorCodeMsg errorCodeMsg, String otherErrorInfo) {
        this.errorCodeMsg = errorCodeMsg;
        this.status = errorCodeMsg.getStatus();
        this.message = errorCodeMsg.getZhMessage() + "【" + otherErrorInfo + "】";
    }

}
