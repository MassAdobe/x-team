package com.x.team.config.springboot.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：GRPC CONFIGURATION CONTENT
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 15:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProperties {

    /**
     * 地址
     */
    private String address;
}
