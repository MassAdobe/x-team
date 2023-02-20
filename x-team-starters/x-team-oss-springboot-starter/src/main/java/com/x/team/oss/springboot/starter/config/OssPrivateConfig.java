package com.x.team.oss.springboot.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 描述：oss save configuration
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = OssPrivateConfig.OSS_PRIVATE_CONFIGURATION_HEADER)
public class OssPrivateConfig {

    public final static String OSS_PRIVATE_CONFIGURATION_HEADER = "oss.private";

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private List<String> permitFormat;
    private String regionId;
    private String roleArn;
    private String roleSessionName;
    private String policy;
}
