package com.example.onlineshop.repository;

import com.example.onlineshop.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    UserEntity user = UserEntity.builder()
            .name("test")
            .roles(new ArrayList<>())
            .password("123")
            .username("testUserName")
            .chatId(11L)
            .build();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        user.setId(1L);
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Test
    void findByIdTest() {
        Optional<UserEntity> user = userRepository.findById(this.user.getId());
        assertTrue(user.isPresent());
    }

    @Test
    void findByUsernameTest() {
        Optional<UserEntity> user = userRepository.findByUsername(this.user.getUsername());
        assertTrue(user.isPresent());
    }

    @Test
    void findByChatId() {
        Optional<UserEntity> user = userRepository.findByChatId(this.user.getChatId());
        assertTrue(user.isPresent());
    }

}