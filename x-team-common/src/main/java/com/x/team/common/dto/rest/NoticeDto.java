package com.x.team.common.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：提醒实体类(ErrorCodeMsg)
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 19:07
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    /**
     * 编码
     */
    private String status;
    /**
     * 消息
     */
    private String msg;
}
