package com.x.team.wechat.springboot.starter.service;

import com.x.team.wechat.springboot.starter.vo.WechatOpenIdRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatPhoneNumReqVo;
import com.x.team.wechat.springboot.starter.vo.WechatAccessTokenRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatPhoneNumRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatUserInfoReqVo;
import com.x.team.wechat.springboot.starter.vo.WechatUserInfoRespVo;

/**
 * 描述：微型小程序验证
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:37
 */
public interface WechatAuthService {

    /**
     * 获取接口调用凭证
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
     */
    WechatAccessTokenRespVo wechatAccessToken();

    /**
     * 获取微信用户的openId
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     *
     * @param jsCode 登录时获取的 code
     */
    WechatOpenIdRespVo wechatOpenId(String jsCode);

    /**
     * 获取微信用户的手机号码
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/phonenumber/phonenumber.getPhoneNumber.html
     */
    WechatPhoneNumRespVo wechatPhoneNum(WechatPhoneNumReqVo wechatPhoneNumRequestVo);

    /**
     * 获取微信用户的用户信息
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
     */
    WechatUserInfoRespVo wechatUserInfo(WechatUserInfoReqVo wechatUserInfoRequestVo);
}
