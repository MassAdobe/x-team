package com.x.team.dingtalk.springboot.starter.dto;

import com.x.team.dingtalk.springboot.starter.constants.DingTalkConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：收到钉钉群消息结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:50
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveDingTalkDto {
    /**
     * 返回码
     */
    private Integer code = 200;
    /**
     * 返回消息体
     */
    private String msg = DingTalkConstants.SUCCESS_MSG;
}
