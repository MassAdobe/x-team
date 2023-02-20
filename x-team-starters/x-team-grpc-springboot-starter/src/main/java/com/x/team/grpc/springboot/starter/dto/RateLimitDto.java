package com.x.team.grpc.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：限速配置
 *
 * @author MassAdobe
 * @date Created in 2023/2/16 16:32
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitDto {
    /**
     * 最大令牌数
     */
    private Integer maxTokens;
    /**
     * 令牌填充时间(秒)
     */
    private Long fillInterval;
    /**
     * 填充时间内添加的令牌数
     */
    private Integer tokensPerFill;
}
