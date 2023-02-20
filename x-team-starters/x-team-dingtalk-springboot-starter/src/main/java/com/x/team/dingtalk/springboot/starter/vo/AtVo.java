package com.x.team.dingtalk.springboot.starter.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 描述：At人员结构体
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:51
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AtVo {
    /**
     * 手机号列表
     */
    private List<String> atMobiles;
    /**
     * 人员ID列表
     */
    private List<String> atUserIds;
    /**
     * 是否At所有人
     */
    private Boolean isAtAll = Boolean.FALSE;
}
