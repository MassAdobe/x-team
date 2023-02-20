package com.x.team.wechat.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：获取微信用户的openId响应结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:38
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WechatOpenIdRespVo {
    /**
     * 用户唯一标识
     */
    private String openId;
    /**
     * 会话密钥
     */
    private String sessionKey;
    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回
     * <p>
     * 详见: https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/union-id.html
     */
    private String unionid;
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误信息
     */
    private String errmsg;
}
