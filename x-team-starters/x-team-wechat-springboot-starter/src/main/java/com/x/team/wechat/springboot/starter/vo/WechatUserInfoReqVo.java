package com.x.team.wechat.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：获取微信用户的用户信息请求结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:39
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WechatUserInfoReqVo {
    /**
     * 数据
     */
    private String rawData;
}
