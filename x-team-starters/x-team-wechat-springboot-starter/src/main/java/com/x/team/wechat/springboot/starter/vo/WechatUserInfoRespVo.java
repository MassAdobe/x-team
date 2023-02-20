package com.x.team.wechat.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：获取微信用户的用户信息响应结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:39
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WechatUserInfoRespVo {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 语言
     */
    private String language;
    /**
     * 城市
     */
    private String city;
    /**
     * 省份
     */
    private String province;
    /**
     * 国家
     */
    private String country;
    /**
     * 头像地址
     */
    private String avatarUrl;
}
