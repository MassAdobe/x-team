package com.x.team.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 描述：Base64 utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:47
 */
public class Base64Utils {

    private final static String DES = "DES";
    private final static String ENCODE = "GBK";

    /**
     * 描述：encrypt JDK1.8
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:39 上午
     */
    public static String encode(String str) {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        return new String(encodeBytes);
    }

    /**
     * 描述：decrypt JDK1.8
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:39 上午
     */
    public static String decode(String str) {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
        return new String(decodeBytes);
    }

    /**
     * 描述：encrypt JDK1.8
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:38 上午
     */
    public static String encodeThrowsException(String str) {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        return new String(encodeBytes);
    }

    /**
     * 描述：decrypt JDK1.8
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:38 上午
     */
    public static String decodeThrowsException(String str) {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
        return new String(decodeBytes);
    }

    /**
     * 描述：encrypt according to key and value
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:40 上午
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // generate a credible random number
        SecureRandom sr = new SecureRandom();
        // according to origin key create DES keySpec object
        DESKeySpec dks = new DESKeySpec(key);
        // create a key factory, then let KES keySpec transact to secretKey object
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secureKey = keyFactory.generateSecret(dks);
        // cipher completed the job of encrypt
        Cipher cipher = Cipher.getInstance(DES);
        // user key init cipher object
        cipher.init(Cipher.ENCRYPT_MODE, secureKey, sr);
        return cipher.doFinal(data);
    }

    /**
     * 描述：decrypt according to key and value
     *
     * @author MassAdobe
     * @date Created in 2021/8/19 11:40 上午
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // generate a credible random number
        SecureRandom sr = new SecureRandom();
        // according to origin key create DES keySpec object
        DESKeySpec dks = new DESKeySpec(key);
        // create a key factory, then let KES keySpec transact to secretKey object
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        // cipher completed the job of encrypt
        Cipher cipher = Cipher.getInstance(DES);
        // user key init cipher object
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        return cipher.doFinal(data);
    }

}
