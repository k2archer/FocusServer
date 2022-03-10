package com.k2archer.server.tomato.controller;

import com.google.gson.Gson;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.server.tomato.bean.response.ResponseBody;
import com.k2archer.server.tomato.bean.dto.LoginInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wc;

    @BeforeEach
    public void beforeSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wc).build();
    }


    @Test
    void login() throws Exception {

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername("kwei");
        loginInfo.setPassword("123");

        MvcResult result = null;
        String boy = null;

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)  //数据的格式
                .content(new Gson().toJson(loginInfo));

        result = this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        boy = result.getResponse().getContentAsString();
        ResponseBody response = new Gson().fromJson(boy, ResponseBody.class);

        //断言 是否和预期相等
        assertEquals(response.getCode(), ResponseStateCode.SUCCESS.getCode());
        assertNotNull(response.getData());
    }


    @Test
    public void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, Charset.forName("UTF-8"));
//        String user = "{\"name\": \"bob\", \"email\" : \"bob@domain.com\"}";

        LoginInfo loginInfo = new LoginInfo();
//        loginInfo.setUsername("kwei");
//        loginInfo.setPassword("1123");
        mockMvc.perform(post("/api/user/login")
                .content(new Gson().toJson(loginInfo))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
//                .andExpect(MockMvcResultMatchers.content().contentType(textPlainUtf8));


    }

}