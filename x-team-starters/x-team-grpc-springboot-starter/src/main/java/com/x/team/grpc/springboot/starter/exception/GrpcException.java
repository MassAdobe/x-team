package com.x.team.grpc.springboot.starter.exception;

import com.x.team.common.enums.ErrorCodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：exception grpc encapsulation class
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:09
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GrpcException extends RuntimeException {

    private ErrorCodeMsg errorCodeMsg;
}
