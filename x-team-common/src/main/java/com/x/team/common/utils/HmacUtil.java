package com.x.team.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 描述：Hmac加密工具类
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:47
 */
public class HmacUtil {

    public static String encode(String seed, String source) throws Exception {
        SecretKey secretKey = new SecretKeySpec(seed.getBytes(StandardCharsets.UTF_8), "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKey);
        mac.update(source.getBytes(StandardCharsets.UTF_8));
        return encodeBase64(mac.doFinal());
    }

    private static String encodeBase64(byte[] source) {
        return new String(Base64.encodeBase64(source), StandardCharsets.UTF_8);
    }
}
