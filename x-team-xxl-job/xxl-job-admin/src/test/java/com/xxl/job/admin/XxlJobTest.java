package com.xxl.job.admin;

import org.springframework.util.DigestUtils;

/**
 * 描述：测试类
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 11:08
 */
public class XxlJobTest {

    /**
     * 描述：输出密码
     *
     * @author MassAdobe
     * @date Created in 2023/2/20 11:08
     */
    public static void main(String[] args) {
        // md5 password
        String password = "x-team-123-321";
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(md5Password);
        String ans = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(ans);
    }
}
