package com.x.team.test.schedule.vo.useApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 描述：请求结构体
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 14:59
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestVo {
    private String firstId;
    private Boolean second;
    private List<ThirdVo> third;
}
