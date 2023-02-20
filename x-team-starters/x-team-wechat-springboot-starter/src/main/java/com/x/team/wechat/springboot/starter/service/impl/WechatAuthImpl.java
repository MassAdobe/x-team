package com.x.team.wechat.springboot.starter.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.team.common.constants.CommonConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.common.utils.CommonUtils;
import com.x.team.common.utils.JsonUtils;
import com.x.team.http.springboot.starter.client.HttpClient;
import com.x.team.redis.springboot.starter.client.RedisClient;
import com.x.team.wechat.springboot.starter.constants.WechatConstants;
import com.x.team.wechat.springboot.starter.exception.WechatException;
import com.x.team.wechat.springboot.starter.vo.WechatOpenIdRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatPhoneNumReqVo;
import com.x.team.wechat.springboot.starter.config.WechatConfig;
import com.x.team.wechat.springboot.starter.service.WechatAuthService;
import com.x.team.wechat.springboot.starter.utils.WechatUtils;
import com.x.team.wechat.springboot.starter.vo.WechatAccessTokenRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatPhoneNumRespVo;
import com.x.team.wechat.springboot.starter.vo.WechatUserInfoReqVo;
import com.x.team.wechat.springboot.starter.vo.WechatUserInfoRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：微信小程序验证接口实现类
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:37
 */
@Slf4j
@Service
public class WechatAuthImpl implements WechatAuthService {

    /**
     * 获取open-id的url
     */
    @Value("${wechat.url.open-id:https://api.weixin.qq.com/sns/jscode2session}")
    private String openIdUrl;

    /**
     * 获取Access-Token的url
     */
    @Value("${wechat.url.access-token:https://api.weixin.qq.com/cgi-bin/token}")
    private String accessTokenUrl;

    /**
     * 获取手机号码的url
     */
    @Value("${wechat.url.phone-info:https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s}")
    private String phoneInfoUrl;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /**
     * 获取Open-id请求Map
     */
    private final static Map<String, String> OPEN_ID_MAP = new HashMap<>(4);
    /**
     * 获取Access-token请求Map
     */
    private final static Map<String, String> ACCESS_TOKEN_MAP = new HashMap<>(4);
    /**
     * 获取Open-id的授权种类
     */
    private final static String OPEN_ID_GRANT_TYPE = "authorization_code";
    /**
     * 获取Access-token的授权种类
     */
    private final static String ACCESS_TOKEN_GRANT_TYPE = "client_credential";
    /**
     * 获取phone的请求结构体
     */
    private final static String PHONE_INFO_REQUEST_STRUCT = "{\"access_token\": \"%s\", \"code\": \"%s\"}";

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OPEN_ID_MAP.put(WechatConstants.WECHAT_GRANT_TYPE_KEY, OPEN_ID_GRANT_TYPE);
        ACCESS_TOKEN_MAP.put(WechatConstants.WECHAT_GRANT_TYPE_KEY, ACCESS_TOKEN_GRANT_TYPE);
    }

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private WechatConfig wechatConfig;

    /**
     * 获取接口调用凭证
     */
    @SuppressWarnings("all")
    @Override
    public WechatAccessTokenRespVo wechatAccessToken() {
        String accessToken;
        if (this.redisClient.hasKey(WechatConstants.WECHAT_ACCESS_TOKEN_KEY)) {
            // redis中存在，直接获取即可
            accessToken = (String) this.redisClient.get(WechatConstants.WECHAT_ACCESS_TOKEN_KEY);
            try {
                Map<String, Object> map = OBJECT_MAPPER.readValue(accessToken, Map.class);
                if (map.containsKey("errcode")) {
                    return WechatAccessTokenRespVo.builder()
                            .errcode((int) map.get("errcode"))
                            .errmsg((String) map.get("errmsg"))
                            .build();
                }
                return WechatAccessTokenRespVo.builder()
                        .accessToken((String) map.get("access_token"))
                        .expiresIn((int) map.get("expires_in"))
                        .build();
            } catch (JsonProcessingException e) {
                log.error("【获取接口调用凭证】: 当前获取接口凭证, json解析错误, 内容: {}", accessToken);
                throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
            }
        } else {
            // redis中不存在，需要重新获取
            return this.getAccessToken();
        }
    }

    /**
     * 获取相关微信小程序的Access-Token
     */
    @SuppressWarnings("all")
    private synchronized WechatAccessTokenRespVo getAccessToken() {
        if (!this.redisClient.hasKey(WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY)) {
            for (int i = 0; i < CommonUtils.getRandomNumBetween(WechatConstants.SPIN_BOTTOM, WechatConstants.SPIN_UPPER); i++) {
                // 自旋(doing nothing)
            }
            if (!this.redisClient.hasKey(WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY)) {
                // 当前无锁，直接上锁
                this.redisClient.set(WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY,
                        CommonConstants.EMPTY,
                        WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_TIMEOUT);
                // 获取相关Access-Token
                ACCESS_TOKEN_MAP.put("appid", this.wechatConfig.getAppId());
                ACCESS_TOKEN_MAP.put("secret", this.wechatConfig.getAppSecret());
                String ans = this.httpClient.doGet(this.accessTokenUrl, ACCESS_TOKEN_MAP, null);
                log.info("【获取相关微信小程序的Access-Token】: 结果: {}", ans);
                WechatAccessTokenRespVo wechatAccessTokenResponseVo;
                try {
                    Map<String, Object> map = OBJECT_MAPPER.readValue(ans, Map.class);
                    if (map.containsKey("errcode")) {
                        wechatAccessTokenResponseVo = WechatAccessTokenRespVo.builder()
                                .errcode((int) map.get("errcode"))
                                .errmsg((String) map.get("errmsg"))
                                .build();
                    } else {
                        wechatAccessTokenResponseVo = WechatAccessTokenRespVo.builder()
                                .accessToken((String) map.get("access_token"))
                                .expiresIn((int) map.get("expires_in"))
                                .build();
                    }
                } catch (JsonProcessingException e) {
                    log.error("【获取相关微信小程序的Access-Token】: 当前获取接口凭证, json解析错误, 内容: {}", ans);
                    throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
                }
                if (Objects.nonNull(wechatAccessTokenResponseVo.getErrcode())
                        && 0 != wechatAccessTokenResponseVo.getErrcode()
                        && !StringUtils.isEmpty(wechatAccessTokenResponseVo.getErrmsg())) {
                    log.error("【获取相关微信小程序的Access-Token】: 当前获取接口凭证, 错误: {}, 内容: {}", wechatAccessTokenResponseVo.getErrmsg(), ans);
                    throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, wechatAccessTokenResponseVo.getErrmsg());
                }
                this.redisClient.set(WechatConstants.WECHAT_ACCESS_TOKEN_KEY, ans, WechatConstants.WECHAT_ACCESS_TOKEN_EXPIRE_TIME);
                return wechatAccessTokenResponseVo;
            }
        }
        do {
            for (int i = 0; i < CommonUtils.getRandomNumBetween(WechatConstants.SPIN_BOTTOM, WechatConstants.SPIN_UPPER); i++) {
                // 自旋(doing nothing)
            }
        } while (this.redisClient.hasKey(WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY));
        String ans = null;
        try {
            ans = (String) this.redisClient.get(WechatConstants.WECHAT_ACCESS_TOKEN_GETTING_LOCK_KEY);
            return OBJECT_MAPPER.readValue(ans, WechatAccessTokenRespVo.class);
        } catch (JsonProcessingException e) {
            log.error("【获取相关微信小程序的Access-Token】: 当前获取接口凭证, json解析错误, 内容: {}", ans);
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
        }
    }

    /**
     * 获取微信用户的openId
     *
     * @param jsCode 登录时获取的 code
     */
    @SuppressWarnings("all")
    @Override
    public WechatOpenIdRespVo wechatOpenId(String jsCode) {
        if (!StringUtils.isEmpty(jsCode)) {
            OPEN_ID_MAP.put("appid", this.wechatConfig.getAppId());
            OPEN_ID_MAP.put("secret", this.wechatConfig.getAppSecret());
            OPEN_ID_MAP.put("js_code", jsCode);
            String ans = this.httpClient.doGet(this.openIdUrl, OPEN_ID_MAP, null);
            log.info("【获取微信用户的openId】: 结果: {}", ans);
            if (StringUtils.isEmpty(ans)) {
                log.error("【获取微信用户的openId】: 结果: {}", "空");
                throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "未获取到任何回馈");
            }
            WechatOpenIdRespVo wechatOpenIdResponseVo;
            try {
                Map<String, Object> map = OBJECT_MAPPER.readValue(ans, Map.class);
                if (map.containsKey("session_key") && map.containsKey("openid")) {
                    wechatOpenIdResponseVo = WechatOpenIdRespVo.builder()
                            .sessionKey((String) map.get("session_key"))
                            .openId((String) map.get("openid"))
                            .build();
                } else {
                    wechatOpenIdResponseVo = WechatOpenIdRespVo.builder()
                            .errcode((int) map.get("errcode"))
                            .errmsg((String) map.get("errmsg"))
                            .build();
                }
            } catch (JsonProcessingException e) {
                log.error("【获取微信用户的openId】: 获取openId, json解析错误, 内容: {}", ans);
                throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
            }
            if (Objects.nonNull(wechatOpenIdResponseVo.getErrcode())
                    && 0 != wechatOpenIdResponseVo.getErrcode()
                    && !StringUtils.isEmpty(wechatOpenIdResponseVo.getErrmsg())) {
                log.error("【获取微信用户的openId】: 获取openId, 错误: {}, 内容: {}", wechatOpenIdResponseVo.getErrmsg(), ans);
                throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, wechatOpenIdResponseVo.getErrmsg());
            }
            // redis中存储相关数值
            this.redisClient.set(WechatConstants.WECHAT_EXCHANGE_PHONE_REDIS_KEY + wechatOpenIdResponseVo.getOpenId(),
                    wechatOpenIdResponseVo.getSessionKey(),
                    WechatConstants.WECHAT_EXCHANGE_PHONE_REDIS_TIMEOUT);
            return wechatOpenIdResponseVo;
        }
        log.error("【获取微信用户的openId】: jsCode为空");
        throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(jsCode不能为空)");
    }

    /**
     * 获取微信用户的手机号码
     */
    @SuppressWarnings("all")
    @Override
    public WechatPhoneNumRespVo wechatPhoneNum(WechatPhoneNumReqVo wechatPhoneNumRequestVo) {
        if (StringUtils.isEmpty(wechatPhoneNumRequestVo.getOpenId())
                || StringUtils.isEmpty(wechatPhoneNumRequestVo.getRawData())
                || StringUtils.isEmpty(wechatPhoneNumRequestVo.getIv())
        ) {
            log.error("【获取微信用户的手机号码】: 当前请求参数存在问题");
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(当前数据不完整)");
        }
        String sessionKey;
        if (this.redisClient.hasKey(WechatConstants.WECHAT_EXCHANGE_PHONE_REDIS_KEY + wechatPhoneNumRequestVo.getOpenId())) {
            // 存在相关值
            sessionKey = (String) this.redisClient.get(WechatConstants.WECHAT_EXCHANGE_PHONE_REDIS_KEY + wechatPhoneNumRequestVo.getOpenId());
        } else {
            log.error("【获取微信用户的手机号码】: 当前session_key已经过期, openId: {}", wechatPhoneNumRequestVo.getOpenId());
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(当前不存在相关微信服务的计算内容，需要重新登录)");
        }
        log.info("【获取微信用户的手机号码】: 当前用户, openId: {}, session数据: {}", wechatPhoneNumRequestVo.getOpenId(), sessionKey);
        // 调用获取access-token获取相关用户手机号码
        String response = WechatUtils.decryptData(wechatPhoneNumRequestVo.getRawData(), sessionKey, wechatPhoneNumRequestVo.getIv());
        if (Objects.isNull(response)) {
            log.error("【获取微信用户的手机号码】: 当前解密错误, openId: {}", wechatPhoneNumRequestVo.getOpenId());
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(解密错误)");
        }
        if (!StringUtils.isEmpty(response)) {
            try {
                Map<String, Object> map = JsonUtils.OBJECT_MAPPER.readValue(response, Map.class);
                return WechatPhoneNumRespVo.builder()
                        .phoneNumber((String) map.get("phoneNumber"))
                        .purePhoneNumber((String) map.get("purePhoneNumber"))
                        .countryCode((String) map.get("countryCode"))
                        .build();
            } catch (JsonProcessingException e) {
                log.error("【获取微信用户的手机号码】: 获取openId, json解析错误, 内容: {}", response);
                throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
            }
        }
        log.error("【获取微信用户的手机号码】: 当前不存在用户相关OPEN-ID小程序授权值, openId: {}", wechatPhoneNumRequestVo.getOpenId());
        throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(小程序授权不存在相关预设值)");
    }

    /**
     * 获取微信用户的用户信息
     */
    @SuppressWarnings("all")
    @Override
    public WechatUserInfoRespVo wechatUserInfo(WechatUserInfoReqVo wechatUserInfoRequestVo) {
        if (StringUtils.isEmpty(wechatUserInfoRequestVo.getRawData())) {
            log.error("【获取微信用户的用户信息】: 当前请求参数存在问题");
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(当前数据不完整)");
        }
        try {
            Map<String, Object> map = JsonUtils.OBJECT_MAPPER.readValue(wechatUserInfoRequestVo.getRawData(), Map.class);
            return WechatUserInfoRespVo.builder()
                    .nickName((String) map.get("nickName"))
                    .avatarUrl((String) map.get("avatarUrl"))
                    .city((String) map.get("city"))
                    .country((String) map.get("country"))
                    .language((String) map.get("language"))
                    .province((String) map.get("province"))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("【获取微信用户的用户信息】: 获取openId, json解析错误, 内容: {}", wechatUserInfoRequestVo.getRawData());
            throw new WechatException(ErrorCodeMsg.WECHAT_ERROR, "(JSON解析错误)");
        }
    }

    public void setOpenIdUrl(String openIdUrl) {
        this.openIdUrl = openIdUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

}
