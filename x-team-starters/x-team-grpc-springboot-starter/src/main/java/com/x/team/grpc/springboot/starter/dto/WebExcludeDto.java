package com.x.team.grpc.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：过滤不需要拦截的接口
 *
 * @author MassAdobe
 * @date Created in 2023/2/16 16:27
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WebExcludeDto {
    /**
     * 接口名称(URL)
     */
    private String url;
    /**
     * 限速配置
     */
    private RateLimitDto rateLimit;
}
