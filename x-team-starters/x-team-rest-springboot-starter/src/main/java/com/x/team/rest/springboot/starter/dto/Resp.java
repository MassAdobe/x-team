package com.x.team.rest.springboot.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.x.team.common.constants.CommonConstants;
import com.x.team.common.constants.RegexConstants;
import com.x.team.common.context.HeaderContext;
import com.x.team.common.dto.grpc.header.UserContext;
import com.x.team.common.dto.rest.NoticeDto;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.log.springboot.starter.utils.XTeamLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 描述：返回消息体
 *
 * @author MassAdobe
 * @date Created in 2021/8/19 2:08 下午
 */
@SuppressWarnings("all")
@Slf4j
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resp<T> implements Serializable {

    private static final long serialVersionUID = -5819746795237643688L;

    private final static ObjectMapper OBJECT_MAPPER;

    /**
     * 空回复
     */
    private final static String NULL_REPLY = "{\"status\": \"%s\", \"msg\": \"%s\", \"data\": null}";

    /**
     * 有值回复
     */
    private final static String COMPLETE_REPLY = "{\"status\": \"%s\", \"msg\": \"%s\", \"data\": %s}";

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * return code
     */
    private String status;

    /**
     * return info
     */
    private String msg;

    /**
     * return data
     */
    private T data;

    /**
     * clazz
     */
    private Class<T> clazz;

    /**
     * return data (JSON STRING)
     */
    private String resp;

    public Resp(String status, String msg, String resp) {
        this.status = status;
        this.msg = msg;
        this.resp = resp;
    }

    public static Resp success() {
        UserContext userContext = HeaderContext.getContext();
        NoticeDto noticeDto = ErrorCodeMsg.getError(userContext.getRequestContext().getI18n(), ErrorCodeMsg.SUCCESS);
        String format = String.format(NULL_REPLY,
                noticeDto.getStatus(),
                noticeDto.getMsg());
        Resp rtn = null;
        try {
            rtn = OBJECT_MAPPER.readValue(format, Resp.class);
        } catch (JsonProcessingException e) {
            log.error("[rest-resp]: success json error, error: {}", e);
        }
        return rtn;
    }

    public static <T> Resp<T> success(T data) {
        UserContext userContext = HeaderContext.getContext();
        NoticeDto noticeDto = ErrorCodeMsg.getError(userContext.getRequestContext().getI18n(), ErrorCodeMsg.SUCCESS);
        try {
            String print = JsonFormat.printer().print((MessageOrBuilder) data);
            String format = String.format(COMPLETE_REPLY,
                    noticeDto.getStatus(),
                    noticeDto.getMsg(),
                    print.replaceAll(RegexConstants.T_N_S_R, CommonConstants.EMPTY));
            return OBJECT_MAPPER.readValue(format, Resp.class);
        } catch (InvalidProtocolBufferException | JsonProcessingException e) {
            log.error("[rest-resp]: success<T> json error, error: {}", e);
        }
        return success();
    }

    public static Resp failure(ErrorCodeMsg errorCodeMsg) {
        UserContext userContext = HeaderContext.getContext();
        NoticeDto noticeDto = ErrorCodeMsg.getError(userContext.getRequestContext().getI18n(), errorCodeMsg);
        String format = String.format(NULL_REPLY,
                noticeDto.getStatus(),
                noticeDto.getMsg());
        Resp rtn = null;
        try {
            rtn = OBJECT_MAPPER.readValue(format, Resp.class);
        } catch (JsonProcessingException e) {
            log.error("[rest-resp]: failure json error, error: {}", e);
        }
        return rtn;
    }

    public static <T> Resp<T> failure(ErrorCodeMsg errorCodeMsg, T data) {
        UserContext userContext = HeaderContext.getContext();
        NoticeDto noticeDto = ErrorCodeMsg.getError(userContext.getRequestContext().getI18n(), errorCodeMsg);
        try {
            String print = JsonFormat.printer().print((MessageOrBuilder) data);
            String format = String.format(COMPLETE_REPLY,
                    noticeDto.getStatus(),
                    noticeDto.getMsg(),
                    print.replaceAll(RegexConstants.T_N_S_R, CommonConstants.EMPTY));
            return OBJECT_MAPPER.readValue(format, Resp.class);
        } catch (InvalidProtocolBufferException | JsonProcessingException e) {
            log.error("[rest-resp]: failure<T> json error, error: {}", e);
        }
        return failure(errorCodeMsg);
    }

    public Resp() {
        //获取泛型的class类型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            this.clazz = (Class<T>) actualTypeArguments[0];
        }
    }

    /**
     * 删除所有系统线程内容
     */
    public static void removeThreadLocal() {
        XTeamLog.removeBegin();
        HeaderContext.clearContext();
    }
}
