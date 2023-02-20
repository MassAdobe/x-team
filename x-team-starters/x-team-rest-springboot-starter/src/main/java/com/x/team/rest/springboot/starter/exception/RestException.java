package com.x.team.rest.springboot.starter.exception;

import com.x.team.common.enums.ErrorCodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 描述：exception encapsulation class
 *
 * @author MassAdobe
 * @date Created in 2023/1/29 14:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestException extends RuntimeException {

    private ErrorCodeMsg errorCodeMsg;
}
