package com.x.team.wechat.springboot.starter.exception;

import com.x.team.common.enums.ErrorCodeMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：小程序错误
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatException extends RuntimeException {

    private ErrorCodeMsg errorCodeMsg;
    private String status;
    private String zhMessage;
    private String enMessage;

    public WechatException(ErrorCodeMsg errorCodeMsg) {
        this.errorCodeMsg = errorCodeMsg;
        this.status = errorCodeMsg.getStatus();
        this.zhMessage = errorCodeMsg.getZhMessage();
        this.enMessage = errorCodeMsg.getEnMessage();
    }

    public WechatException(ErrorCodeMsg errorCodeMsg, String otherErrorInfo) {
        this.errorCodeMsg = errorCodeMsg;
        this.status = errorCodeMsg.getStatus();
        this.zhMessage = errorCodeMsg.getZhMessage() + "【" + otherErrorInfo + "】";
        this.enMessage = errorCodeMsg.getEnMessage() + "【" + otherErrorInfo + "】";
    }

}

