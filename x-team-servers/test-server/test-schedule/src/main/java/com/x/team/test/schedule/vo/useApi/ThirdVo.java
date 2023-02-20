package com.x.team.test.schedule.vo.useApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：内部列表结构体
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 15:00
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ThirdVo {
    private String thirdFirst;
    private Integer thirdSecond;
}
