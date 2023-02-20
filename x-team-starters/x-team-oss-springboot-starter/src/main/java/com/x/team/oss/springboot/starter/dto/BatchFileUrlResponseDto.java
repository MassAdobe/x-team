package com.x.team.oss.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：批量获取文件路径地址
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:08
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileUrlResponseDto {
    /**
     * 文件夹名称
     */
    private String dirName;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * url
     */
    private String url;
    /**
     * oss主键ID
     */
    private Integer ossId;
}
