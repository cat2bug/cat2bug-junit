package com.cat2bug.junit.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-24 23:42
 * @Version: 1.0.0
 */
@Data
@AllArgsConstructor
public class UserVo {
    private Long id;
    private String name;
    private String password;
}
