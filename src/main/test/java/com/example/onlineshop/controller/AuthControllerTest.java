package com.example.onlineshop.controller;

import com.example.onlineshop.OnlineShopApplication;
import com.example.onlineshop.entity.dto.AuthDto;
import com.example.onlineshop.service.CategoryService;
import com.example.onlineshop.service.UserService;
import jakarta.ws.rs.core.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = OnlineShopApplication.class
)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Autowired
    private HomePageController homePageController;
    private AutoCloseable autoCloseable;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void loginPageTest() throws Exception {
        mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
    }

    @Test
    void loginPageWithWrongUrlTest() throws Exception {
        mockMvc.perform(get("/auth/wrongUrl")).andExpect(status().isNotFound());
    }

    @Test
    void loginTest() throws Exception {
        AuthDto authDto = new AuthDto("test","1234");
        MockHttpServletResponse response = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authDto.toString())
        ).andDo((ResultHandler) verify(userService).validUser(authDto.getUsername(), authDto.getPassword(), "login")).andReturn().getResponse();
        String path = response.getContentAsString();
        assertEquals(path,"menu");
    }
}