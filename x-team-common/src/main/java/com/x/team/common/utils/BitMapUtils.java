package com.x.team.common.utils;

/**
 * 描述：BitMap utils
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:47
 */
@SuppressWarnings("all")
public class BitMapUtils {


    /**
     * 描述：length of bitmap，the actual operable subscript is [0,length)
     *
     * @author 月关
     * @date Created in 2021/11/2 2:35 下午
     */
    public static byte[] create(int length) {
        return new byte[length % 8 == 0 ? length / 8 : length / 8 + 1];
    }

    /**
     * 描述：set the value of the specified bit
     *
     * @author 月关
     * @date Created in 2021/11/2 2:35 下午
     */
    public static byte[] set(byte[] bytes, int index, boolean value) {
        if (value) {
            bytes[index >> 3] |= 1 << (index & 7);
        } else {
            bytes[index >> 3] &= ~(1 << (index & 7));
        }
        return bytes;
    }

    /**
     * 描述：get the value of the specified bit
     *
     * @author 月关
     * @date Created in 2021/11/2 2:35 下午
     */
    public static boolean get(byte[] bytes, int index) {
        int i = index & 7;
        return !((bytes[index >> 3] & (0b11111111 >>> (7 - i))) >> i == 0);
    }
}