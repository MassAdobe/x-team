package com.x.team.dingtalk.springboot.starter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.x.team.common.utils.JsonUtils;
import com.x.team.dingtalk.springboot.starter.constants.DingTalkConstants;
import com.x.team.dingtalk.springboot.starter.service.DingTalkService;
import com.x.team.dingtalk.springboot.starter.utils.DingTalkEncodeUtils;
import com.x.team.dingtalk.springboot.starter.dto.ReceiveDingTalkDto;
import com.x.team.dingtalk.springboot.starter.dto.SendDingTalkDto;
import com.x.team.dingtalk.springboot.starter.vo.AtVo;
import com.x.team.dingtalk.springboot.starter.vo.TextVo;
import com.x.team.dingtalk.springboot.starter.vo.TrueSendingRequestVo;
import com.x.team.dingtalk.springboot.starter.vo.TrueSendingResponseVo;
import com.x.team.http.springboot.starter.client.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：钉钉消息接口实现类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:50
 */
@Slf4j
@Service
public class DingTalkServiceImpl implements DingTalkService {

    /**
     * 调用配置列表
     */
    private final static Map<String, String[]> CALLING_MAP = new ConcurrentHashMap<>();

    @Autowired
    private HttpClient httpClient;

    /**
     * 发送消息
     */
    @SuppressWarnings("all")
    @Override
    public ReceiveDingTalkDto sendMessage(SendDingTalkDto sendDingTalkDto) {
        // 校验数据
        if (Objects.isNull(sendDingTalkDto)
                || StringUtils.isEmpty(sendDingTalkDto.getAccessToken())
                || StringUtils.isEmpty(sendDingTalkDto.getDingTalkSecret())
                || StringUtils.isEmpty(sendDingTalkDto.getMessageText())) {
            log.error("【DING-TALK】: 发送消息, 参数错误, {}", sendDingTalkDto);
            return ReceiveDingTalkDto.builder()
                    .code(500)
                    .msg(DingTalkConstants.FAIL_PARAM_ERROR)
                    .build();
        }
        // 默认给定不需要at所有人
        if (Objects.isNull(sendDingTalkDto.getNeedAtAll())) {
            sendDingTalkDto.setNeedAtAll(Boolean.FALSE);
        }
        // 获取请求URL
        String callUrl;
        // 获取是否存在原来的数据
        if (CALLING_MAP.containsKey(sendDingTalkDto.getDingTalkSecret())) {
            // 存在 获取校验
            String[] callingInfo = CALLING_MAP.get(sendDingTalkDto.getDingTalkSecret());
            long l = (System.currentTimeMillis() - Long.parseLong(callingInfo[0])) / 1000;
            if (l > DingTalkConstants.ENCODING_EXPIRE_TIME_PERIOD) {
                // 相差的时间已经超过约定时间 需要替换算法
                String[] timestampAndCode = DingTalkEncodeUtils.getTimestampAndCode(sendDingTalkDto.getDingTalkSecret());
                if (Objects.isNull(timestampAndCode)) {
                    log.error("【DING-TALK】: 发送消息, 加密错误, {}", sendDingTalkDto);
                    return ReceiveDingTalkDto.builder()
                            .code(500)
                            .msg(DingTalkConstants.FAIL_ENCODE_ERROR)
                            .build();
                }
                // 获取请求URL
                callUrl = String.format(DingTalkConstants.DING_TALK_CALL_URL, sendDingTalkDto.getAccessToken(), timestampAndCode[0], timestampAndCode[1]);
                // 存入容器(timestamp和调用地址)
                CALLING_MAP.put(sendDingTalkDto.getDingTalkSecret(), new String[]{timestampAndCode[0], callUrl});
            } else {
                // 直接用
                callUrl = callingInfo[1];
            }
        } else {
            // 不存在 新建
            String[] timestampAndCode = DingTalkEncodeUtils.getTimestampAndCode(sendDingTalkDto.getDingTalkSecret());
            if (Objects.isNull(timestampAndCode)) {
                log.error("【DING-TALK】: 发送消息, 加密错误, {}", sendDingTalkDto);
                return ReceiveDingTalkDto.builder()
                        .code(500)
                        .msg(DingTalkConstants.FAIL_ENCODE_ERROR)
                        .build();
            }
            // 获取请求URL
            callUrl = String.format(DingTalkConstants.DING_TALK_CALL_URL, sendDingTalkDto.getAccessToken(), timestampAndCode[0], timestampAndCode[1]);
            // 存入容器(timestamp和调用地址)
            CALLING_MAP.put(sendDingTalkDto.getDingTalkSecret(), new String[]{timestampAndCode[0], callUrl});
        }
        String response;
        try {
            // 发送请求
            response = this.httpClient.doPostJson(callUrl,
                    JsonUtils.OBJECT_MAPPER.writeValueAsString(TrueSendingRequestVo.builder()
                            .at(AtVo.builder()
                                    .isAtAll(sendDingTalkDto.getNeedAtAll())
                                    .build())
                            .text(TextVo.builder()
                                    .content(sendDingTalkDto.getMessageText())
                                    .build())
                            .msgtype(DingTalkConstants.DING_TALK_MSG_TYPE_TEXT)
                            .build()), null);
            log.info("【DING-TALK】: 发送消息, 调用结果, {}", response);
        } catch (JsonProcessingException e) {
            log.error("【DING-TALK】: 发送消息, 请求解析JSON错误, {}", sendDingTalkDto);
            return ReceiveDingTalkDto.builder()
                    .code(500)
                    .msg(DingTalkConstants.FAIL_SEND_JSON_ERROR)
                    .build();
        }
        try {
            // 解析返回结果
            TrueSendingResponseVo trueSendingResponseVo = JsonUtils.OBJECT_MAPPER.readValue(response, TrueSendingResponseVo.class);
            if (0 == trueSendingResponseVo.getErrcode()) {
                // 成功
                return ReceiveDingTalkDto.builder()
                        .code(trueSendingResponseVo.getErrcode())
                        .msg(DingTalkConstants.SUCCESS_MSG)
                        .build();
            } else {
                // 失败
                return ReceiveDingTalkDto.builder()
                        .code(trueSendingResponseVo.getErrcode())
                        .msg(trueSendingResponseVo.getErrmsg())
                        .build();
            }
        } catch (JsonProcessingException e) {
            log.error("【DING-TALK】: 发送消息, 响应解析JSON错误, {}", sendDingTalkDto);
            return ReceiveDingTalkDto.builder()
                    .code(500)
                    .msg(DingTalkConstants.FAIL_RECEIVE_JSON_ERROR)
                    .build();
        }
    }
}
