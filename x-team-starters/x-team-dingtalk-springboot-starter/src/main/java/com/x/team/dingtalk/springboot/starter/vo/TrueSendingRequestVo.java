package com.x.team.dingtalk.springboot.starter.vo;

import com.x.team.dingtalk.springboot.starter.constants.DingTalkConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：真实发送结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:51
 */
@SuppressWarnings("all")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrueSendingRequestVo {
    /**
     * @ 人员
     */
    private AtVo at;
    /**
     * 发送内容
     */
    private TextVo text;
    /**
     * 消息种类
     */
    private String msgtype = DingTalkConstants.DING_TALK_MSG_TYPE_TEXT;
}
