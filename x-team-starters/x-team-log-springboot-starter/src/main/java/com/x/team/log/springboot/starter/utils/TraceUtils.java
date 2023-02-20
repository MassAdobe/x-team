package com.x.team.log.springboot.starter.utils;

import com.x.team.common.constants.CommonConstants;

import java.util.Random;

/**
 * 描述：trace id utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:56
 */
public class TraceUtils {

    /**
     * 生成traceId
     */
    public static String generateTraceId() {
        return getRandomString(18) + CommonConstants.HORIZONTAL_MARK + System.currentTimeMillis();
    }

    /**
     * 随机种子
     */
    private final static String RANDOM_SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 描述：generate a->b A->B 0->9 incomming params
     *
     * @author MassAdobe
     * @date Created in 2023/1/20 14:56
     */
    @SuppressWarnings("all")
    private static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(RANDOM_SEED.charAt(number));
        }
        return sb.toString();
    }
}
