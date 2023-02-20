package com.x.team.dingtalk.springboot.starter.utils;

import com.x.team.dingtalk.springboot.starter.constants.DingTalkConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：加密工具类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:50
 */
@Slf4j
public class DingTalkEncodeUtils {

    /**
     * 获取加密数据
     * <p>
     * https://open.dingtalk.com/document/robots/customize-robot-security-settings
     *
     * @return timestamp和encoding
     */
    @SuppressWarnings("all")
    public static String[] getTimestampAndCode(String secret) {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance(DingTalkConstants.ENCODE_TYPE);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), DingTalkConstants.ENCODE_TYPE));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.toString());
            return new String[]{String.valueOf(timestamp), sign};
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.error("【DING-TALK】: 获取加密数据, 加密错误, 错误: {}", e);
            return null;
        }
    }
}
