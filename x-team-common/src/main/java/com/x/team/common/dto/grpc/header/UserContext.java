package com.x.team.common.dto.grpc.header;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：用户信息
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 19:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 其他信息
     */
    private RequestContext requestContext;

    @Override
    public String toString() {
        return "UserContext{" +
                "userId='" + userId + '\'' +
                ", realName='" + realName + '\'' +
                '}';
    }
}
