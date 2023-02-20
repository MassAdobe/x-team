package com.x.team.dingtalk.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：发送钉钉群消息结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:50
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SendDingTalkDto {
    /**
     * 相关accessToken
     */
    private String accessToken;
    /**
     * 秘钥(钉钉群秘钥)
     */
    private String dingTalkSecret;
    /**
     * 发送消息内容
     */
    private String messageText;
    /**
     * 是否需要At所有人
     */
    private Boolean needAtAll = Boolean.FALSE;
}
