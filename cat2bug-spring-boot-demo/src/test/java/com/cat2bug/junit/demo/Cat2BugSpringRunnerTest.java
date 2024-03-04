package com.cat2bug.junit.demo;

import com.cat2bug.junit.Cat2BugSpringRunner;
import com.cat2bug.junit.annotation.PushDefect;
import com.cat2bug.junit.demo.entity.User;
import com.cat2bug.junit.demo.service.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;


/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-25 22:09
 * @Version: 1.0.0
 */
@RunWith(Cat2BugSpringRunner.class)
@PushDefect(host = "http://127.0.0.1:2020", projectKey = "20240225012438h19zzdb6sd1ahazj", handler = "admin")
@WebAppConfiguration
@SpringBootTest
public class Cat2BugSpringRunnerTest {
    private MockMvc mock;
    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    UserRepository userRepository;

    @Before
    public void init() {
        mock = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON);
        mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }
}
