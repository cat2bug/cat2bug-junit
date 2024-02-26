package com.cat2bug.junit.demo.service;

import com.cat2bug.junit.demo.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User getUserByNameAndPassword(String name,String password);
}
