package com.example.onlineshop.repository;

import com.example.onlineshop.entity.HistoryEntity;
import com.example.onlineshop.entity.OrderEntity;
import com.example.onlineshop.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HistoryRepositoryTest {

    UserEntity userEntity = UserEntity.builder()
            .name("test")
            .username("test")
            .password("123")
            .build();

    OrderEntity order = OrderEntity.builder()
            .user(userEntity)
            .build();

    HistoryEntity history = HistoryEntity.builder()
            .orderEntity(order)
            .build();
    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        userEntity.setDeleted(false);
        userEntity.setId(1L);
        userRepository.save(userEntity);
        order.setId(1L);
        order.setDeleted(false);
        orderRepository.save(order);
        history.setId(1L);
        history.setDeleted(false);
        historyRepository.save(history);
    }

    @Test
    void findByIdTest() {
        Optional<HistoryEntity> historyById = historyRepository.findById(history.getId());
        assertTrue(historyById.isPresent());
    }

    @Test
    void findByUserIdTest() {
        List<HistoryEntity> history = historyRepository.findByUserId(userEntity.getId());
        assertTrue(history.size()>0);
    }
}