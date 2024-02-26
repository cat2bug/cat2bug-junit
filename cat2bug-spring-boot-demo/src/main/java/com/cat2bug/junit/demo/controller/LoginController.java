package com.cat2bug.junit.demo.controller;

import com.cat2bug.junit.demo.entity.User;
import com.cat2bug.junit.demo.service.UserRepository;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 22:23
 * @Version: 1.0.0
 */
@RestController
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user)
    {
        Preconditions.checkNotNull(user.getName(),"登陆账号不能为空");
        Preconditions.checkNotNull(user.getPassword(),"登陆密码不能为空");

        return ResponseEntity.ok(this.userRepository.getUserByNameAndPassword(user.getName(), user.getPassword()));
    }
}
