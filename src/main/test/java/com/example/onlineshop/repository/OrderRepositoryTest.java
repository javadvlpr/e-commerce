package com.example.onlineshop.repository;

import com.example.onlineshop.entity.OrderEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    UserEntity user = UserEntity.builder()
            .name("test")
            .username("test")
            .password("123")
            .build();


    OrderEntity order = OrderEntity
            .builder()
            .user(user)
            .orderStatus(OrderStatus.NEW)
            .amount(123)
            .price(123D)
            .build();

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        user.setDeleted(false);
        user.setId(1L);
        userRepository.save(user);
        order.setId(1L);
        order.setDeleted(false);
        orderRepository.save(order);
    }

    @Test
    void getOrderByIdTest() {
        Optional<OrderEntity> order = orderRepository.getOrderById(this.order.getId());
        assertTrue(order.isPresent());
    }

    @Test
    void getOrdersByOwnerId() {
        List<OrderEntity> orders = orderRepository.getOrdersByOwnerId(user.getId());
        assertTrue(orders.size()>0);
    }
}