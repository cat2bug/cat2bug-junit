package com.cat2bug.junit.demo.controller;

import com.cat2bug.junit.demo.vo.UserVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 23:40
 * @Version: 1.0.0
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userId);
    }

//    @RequestMapping
//    public ResponseEntity<?> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUser());
//    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserVo user) {
        UserVo newUser = new UserVo(1L,"admin","password");
        return ResponseEntity.ok(newUser);
    }
//
//    @DeleteMapping("{userId}")
//    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
//        User user = userService.getOneById(userId);
//        if (user == null) {
//            throw new RuntimeException("删除的userId=" + userId + "不存在！");
//        }
//        userService.delete(userId);
//        return ResponseEntity.ok(user);
//    }
//
//    @PutMapping("/{userId}")
//    public ResponseEntity<?> updateUser(@PathVariable String userId,
//                                        @RequestParam(value = "name", required = false) String name,
//                                        @RequestParam(value = "password", required = false) String password) throws Exception {
//        if (name == null || "".equals(name)) {
//            throw new Exception("参数name不能为空!");
//        }
//        if (password == null || "".equals(password)) {
//            throw new Exception("参数password不能为空!");
//        }
//        User user = userService.getOneById(userId);
//        user.setName(name);
//        user.setPassword(password);
//        this.userService.update(user);
//        return ResponseEntity.ok(user);
//    }
}
