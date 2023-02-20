package com.x.team.http.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import okhttp3.Headers;

/**
 * 描述：请求返回相关数据和头
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:30
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostWithHeaderDto {
    /**
     * 返回内容
     */
    private String ans;
    /**
     * 返回头
     */
    private Headers headers;
}
