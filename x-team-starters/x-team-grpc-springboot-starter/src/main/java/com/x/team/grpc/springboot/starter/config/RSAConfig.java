package com.x.team.grpc.springboot.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：RSA (public/private) key configuration
 *
 * @author MassAdobe
 * @date Created in 2023/2/7 17:26
 */
@Data
@Configuration
@ConfigurationProperties(prefix = RSAConfig.RSA_CONFIG_PREFIX)
public class RSAConfig {

    /**
     * configuration prefix
     */
    public final static String RSA_CONFIG_PREFIX = "rsa.token.key";
    /**
     * public key
     */
    private String publicRsa;
    /**
     * private key
     */
    private String privateRsa;
}
