package com.x.team.oss.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：获取STS临时TOKEN响应结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:08
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StsDto {

    private String expiration;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String requestId;
    private String endPoint;
    private String regionId;
    private String bucket;
}
