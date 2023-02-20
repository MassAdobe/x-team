package com.x.team.wechat.springboot.starter.constants;

/**
 * 描述：微信常量池
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:34
 */
public class WechatConstants {
    /**
     * 微信Access_token的redis键
     */
    public final static String WECHAT_ACCESS_TOKEN_KEY = "wechat:access_token";
    /**
     * 微信接口调用凭证失效时间
     */
    public final static Long WECHAT_ACCESS_TOKEN_EXPIRE_TIME = 7000L;
    /**
     * 获取微信小程序Access_token的分布式锁头
     */
    public final static String WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY = "wechat:access_lock";
    /**
     * 获取微信小程序Access_token的分布式锁头过期时间
     */
    public final static Long WECHAT_ACCESS_TOKEN_GETTING_LOCK_TIMEOUT = 30L;
    /**
     * 自旋锁下界
     */
    public final static int SPIN_BOTTOM = 5000;
    /**
     * 自旋上界
     */
    public final static int SPIN_UPPER = 9999;
    /**
     * 微信授权种类键
     */
    public final static String WECHAT_GRANT_TYPE_KEY = "grant_type";
    /**
     * 换取手机号码Redis的key
     */
    public final static String WECHAT_EXCHANGE_PHONE_REDIS_KEY = "wechat:phone:";
    /**
     * 换取手机号码Redis保存时长
     */
    public final static Long WECHAT_EXCHANGE_PHONE_REDIS_TIMEOUT = 360L;
}
