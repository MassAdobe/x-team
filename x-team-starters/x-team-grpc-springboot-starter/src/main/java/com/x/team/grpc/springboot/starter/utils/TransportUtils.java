package com.x.team.grpc.springboot.starter.utils;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 描述：pojo转换工具类
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 16:35
 */
@Slf4j
public class TransportUtils {

    /**
     * 将ProtoBean对象转化为POJO对象
     *
     * @param destPojoClass 目标POJO对象的类类型
     * @param sourceMessage 含有数据的ProtoBean对象实例
     * @param <PojoType>    目标POJO对象的类类型范型
     */
    public static <PojoType> PojoType toPojo(Class<PojoType> destPojoClass, Message sourceMessage) {
        if (Objects.isNull(destPojoClass)) {
            throw new IllegalArgumentException("No destination pojo class specified");
        }
        if (Objects.isNull(sourceMessage)) {
            throw new IllegalArgumentException("No source message specified");
        }
        String json = null;
        try {
            json = JsonFormat.printer().print(sourceMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("[将ProtoBean对象转化为POJO对象]: {}", e.getMessage());
            throw new RuntimeException("PROTO TRANSPORT POJO ERROR");
        }
        return new Gson().fromJson(json, destPojoClass);
    }

    /**
     * 将POJO对象转化为ProtoBean对象
     *
     * @param destBuilder    目标Message对象的Builder类
     * @param sourcePojoBean 含有数据的POJO对象
     */
    public static void toProtoBean(Message.Builder destBuilder, Object sourcePojoBean) {
        if (Objects.isNull(destBuilder)) {
            throw new IllegalArgumentException("No destination message builder specified");
        }
        if (Objects.isNull(sourcePojoBean)) {
            throw new IllegalArgumentException("No source pojo specified");
        }
        String json = new Gson().toJson(sourcePojoBean);
        try {
            JsonFormat.parser().merge(json, destBuilder);
        } catch (InvalidProtocolBufferException e) {
            log.error("[将POJO对象转化为ProtoBean对象]: {}", e.getMessage());
            throw new RuntimeException("POJO TRANSPORT PROTO ERROR");
        }
    }

    /**
     * 将POJO对象转化为ProtoBean对象
     *
     * @param t              需要转换的Builder对象
     * @param sourcePojoBean 含有数据的POJO对象
     * @param <T>            转换Builder泛型
     * @return 返回链式调用
     */
    @SuppressWarnings("all")
    public static <T> T toProto(T t, Object sourcePojoBean) {
        if (Objects.nonNull(sourcePojoBean)) {
            if (t instanceof Message.Builder) {
                Message.Builder x = (Message.Builder) t;
                String json = new Gson().toJson(sourcePojoBean);
                try {
                    JsonFormat.parser().merge(json, x);
                    return (T) x;
                } catch (InvalidProtocolBufferException e) {
                    log.error("[将POJO对象转化为ProtoBean对象]: {}", e.getMessage());
                    throw new RuntimeException("POJO TRANSPORT PROTO ERROR");
                }
            }
        }
        return null;
    }
}
