package com.x.team.dingtalk.springboot.starter.service;

import com.x.team.dingtalk.springboot.starter.dto.ReceiveDingTalkDto;
import com.x.team.dingtalk.springboot.starter.dto.SendDingTalkDto;

/**
 * 描述：钉钉消息接口类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:50
 */
public interface DingTalkService {

    /**
     * 发送消息
     * <p>
     * 一分钟只能调用20次
     * <p>
     * https://open.dingtalk.com/document/robots/custom-robot-access
     */
    ReceiveDingTalkDto sendMessage(SendDingTalkDto sendDingTalkDto);
}
