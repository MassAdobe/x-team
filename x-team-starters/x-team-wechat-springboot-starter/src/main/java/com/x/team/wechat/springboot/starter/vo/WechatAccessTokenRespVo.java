package com.x.team.wechat.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：微型小程序验证相应结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:38
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WechatAccessTokenRespVo {
    /**
     * 获取到的凭证
     */
    private String accessToken;
    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private Integer expiresIn;
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误信息
     */
    private String errmsg;
}
