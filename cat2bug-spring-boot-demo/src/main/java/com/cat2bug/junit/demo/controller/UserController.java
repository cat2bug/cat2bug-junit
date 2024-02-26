package com.cat2bug.junit.demo.controller;

import com.cat2bug.junit.demo.entity.User;
import com.cat2bug.junit.demo.service.UserRepository;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 23:40
 * @Version: 1.0.0
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息
     * @param userId
     * @return  用户数据
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userRepository.findById(userId));
    }

    /**
     * 获取用户列表
     * @return
     */
    @RequestMapping
    public ResponseEntity<?> getUserList() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User newUser = new User(1L,"admin","password");
        return ResponseEntity.ok(this.userRepository.save(user));
    }

    /**
     * 更新用户
     * @param userId
     * @param user
     * @return
     * @throws Exception
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestBody User user) throws Exception {
        Preconditions.checkArgument(Strings.isNotBlank(user.getName()),"参数name不能为空!");
        Preconditions.checkArgument(Strings.isNotBlank(user.getPassword()),"参数password不能为空!");
        Optional<User> u = this.userRepository.findById(userId);
        Preconditions.checkNotNull(u,"没有找到要修改的用户");

        if(Strings.isNotBlank(user.getName())){
            u.get().setName(user.getName());
        }
        if(Strings.isNotBlank(user.getPassword())){
            u.get().setPassword(user.getPassword());
        }

        return ResponseEntity.ok(this.userRepository.save(u.get()));
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        this.userRepository.deleteById(userId);
        return ResponseEntity.ok().body(null);
    }
}
