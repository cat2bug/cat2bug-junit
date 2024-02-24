package com.cat2bug.junit.demo.vo;

import lombok.Data;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 22:24
 * @Version: 1.0.0
 */
@Data
public class LoginBody {
    /**
     * 登陆账号
     */
    private String loginName;
    /**
     * 登陆密码
     */
    private String loginPassword;
}
