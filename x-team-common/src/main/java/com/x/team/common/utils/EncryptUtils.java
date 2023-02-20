package com.x.team.common.utils;


import com.x.team.common.constants.CommonConstants;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 描述：encrypt utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:46
 */
@SuppressWarnings("all")
public class EncryptUtils {

    private final static String AK = "ak";
    private final static String APK = "apk";
    private final static String CT = "ct";

    /**
     * app encrypt content
     */
    public static String appEncrypt(String appPublicKeyStr, String content, String publicKey) throws Exception {
        // base64 server public key to public key object
        PublicKey serverPublicKey = RSAUtils.string2PublicKey(publicKey);
        // generate AES key every time
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        // use server public key encrypt AES key
        byte[] encryptAesKey = RSAUtils.publicEncrypt(aesKeyStr.getBytes(), serverPublicKey);
        // use AES key encrypt app public key
        byte[] encryptAppPublicKey = AESUtils.encryptAES(appPublicKeyStr.getBytes(), aesKey);
        // use AES key encrypt request content
        byte[] encryptRequest = AESUtils.encryptAES(content.getBytes(), aesKey);
        return RSAUtils.byte2Base64(encryptAesKey).replaceAll("\r\n", CommonConstants.EMPTY) + CommonConstants.POT_MARK +
                RSAUtils.byte2Base64(encryptAppPublicKey).replaceAll("\r\n", CommonConstants.EMPTY) + CommonConstants.POT_MARK +
                RSAUtils.byte2Base64(encryptRequest).replaceAll("\r\n", CommonConstants.EMPTY);
    }

    /**
     * app decrypt server reponse content
     */
    public static String appDecrypt(String appPrivateKeyStr, String content) throws Exception {
        String[] split = content.split("\\.");
        // base64 app private key to private key object
        PrivateKey appPrivateKey = RSAUtils.string2PrivateKey(appPrivateKeyStr);
        // use app private key decrpyt AES key
        byte[] aesKeyBytes = RSAUtils.privateDecrypt(RSAUtils.base642Byte(split[0]), appPrivateKey);
        // use AES key decrpyt request content
        SecretKey aesKey = AESUtils.loadKeyAES(new String(aesKeyBytes));
        byte[] response = AESUtils.decryptAES(RSAUtils.base642Byte(split[2]), aesKey);
        return new String(response);
    }

    /**
     * server encrypt reponse app content
     */
    public static String serverEncrypt(String appPublicKeyStr, String content) throws Exception {
        // base64 code app public key to public key object
        PublicKey appPublicKey = RSAUtils.string2PublicKey(appPublicKeyStr);
        // base64 code AES key reverse to secret key object
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        // use app public key encrpyt AES key
        byte[] encryptAesKey = RSAUtils.publicEncrypt(aesKeyStr.getBytes(), appPublicKey);
        // use AES key encrpyt reponse content
        byte[] encryptContent = AESUtils.encryptAES(content.getBytes(), aesKey);
        return RSAUtils.byte2Base64(encryptAesKey).replaceAll("\n", CommonConstants.EMPTY) + CommonConstants.POT_MARK +
                RSAUtils.byte2Base64(encryptContent).replaceAll("\n", CommonConstants.EMPTY);
    }

    /**
     * server decrpyt app request content
     */
    public static String serverDecrypt(String content, String privateKey) throws Exception {
        String[] split = content.split("\\.");
        // base64 code server private key to private key object
        PrivateKey serverPrivateKey = RSAUtils.string2PrivateKey(privateKey);
        // use server private key decrpyt AES key
        byte[] aesKeyBytes = RSAUtils.privateDecrypt(RSAUtils.base642Byte(split[0]), serverPrivateKey);
        // use AES key decrpyt app request
        SecretKey aesKey = AESUtils.loadKeyAES(new String(aesKeyBytes));
        byte[] request = AESUtils.decryptAES(RSAUtils.base642Byte(split[1]), aesKey);
        return new String(request);
    }

}
