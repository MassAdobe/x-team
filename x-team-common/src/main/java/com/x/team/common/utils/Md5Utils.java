package com.x.team.common.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：MD5 utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:48
 */
public class Md5Utils {

    /**
     * MD5的16位加密
     *
     * @param str 加密字符串
     * @return 16位MD5值
     */
    public static String encode16(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            // 16是表示转换为16进制数
            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("md5-16加密失败");
        }
    }

    /**
     * MD5的32位加密
     *
     * @param str 加密字符串
     * @return 16位MD5值
     */
    public static String encode32(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            // 16是表示转换为16进制数
            return new BigInteger(1, digest).toString(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("md5-32加密失败");
        }
    }

}
