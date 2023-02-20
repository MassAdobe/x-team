package com.x.team.dingtalk.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：内容结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:51
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextVo {
    /**
     * 发送文本内容
     */
    private String content;
}
