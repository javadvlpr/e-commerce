package com.example.onlineshop.repository;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.OrderEntity;
import com.example.onlineshop.entity.ProductEntity;
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
class ProductRepositoryTest {

    UserEntity user = UserEntity.builder()
            .name("test")
            .username("test")
            .password("123")
            .build();
    CategoriesEntity categories = CategoriesEntity.builder()
            .hasChild(false)
            .name("test")
            .build();

    ProductEntity product = ProductEntity.builder()
            .param("test")
            .price(14D)
            .amount(14)
            .categories(categories)
            .name("testName")
            .build();

    OrderEntity order = OrderEntity.builder()
            .price(123D)
            .orderStatus(OrderStatus.NEW)
            .amount(123)
            .products(List.of(product))
            .user(user)
            .build();
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setDeleted(false);
        userRepository.save(user);
        categories.setDeleted(false);
        categories.setId(1L);
        categoryRepository.save(categories);
        product.setId(1L);
        product.setDeleted(false);
        productRepository.save(product);
        order.setId(1L);
        order.setDeleted(false);
        orderRepository.save(order);
    }

    @Test
    void getProductsByCategoryIdTest() {
        List<ProductEntity> products = productRepository.getProductsByCategoryId(categories.getId());
        assertFalse(products.isEmpty());
    }

    @Test
    void getByNameTest() {
        Optional<ProductEntity> product = productRepository.getByName(this.product.getName());
        assertTrue(product.isPresent());
    }

    @Test
    void checkProductIsOrderedTest() {
        Optional<ProductEntity> product = productRepository.checkProductIsOrdered(this.product.getId());
        assertTrue(product.isPresent());
    }

    @Test
    void checkProductIsOrderedFalseTest() {
        Optional<ProductEntity> product = productRepository.checkProductIsOrdered(this.product.getId());
        assertTrue(product.isPresent());
    }

    @Test
    void updateMYTest() {
        productRepository.updateMY(product.getAmount(),product.getId());
    }
}