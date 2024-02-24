package com.cat2bug.junit.demo.controller;

import com.cat2bug.junit.demo.vo.AjaxResult;
import com.cat2bug.junit.demo.vo.LoginBody;
import com.google.common.base.Preconditions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 22:23
 * @Version: 1.0.0
 */
//@RestController
public class LoginController {
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        Preconditions.checkNotNull(loginBody.getLoginName(),"登陆账号不能为空");
        Preconditions.checkArgument(
                loginBody.getLoginName().length()<33
                && loginBody.getLoginName().length()>2,"登陆账号长度必须在3至32为之间");

        return AjaxResult.success();
    }
}
