package com.x.team.dingtalk.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：At人员结构体
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
public class TrueSendingResponseVo {
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误消息
     */
    private String errmsg;
}
