package com.example.onlineshop.service;

import com.example.onlineshop.repository.CategoryRepository;
import com.example.onlineshop.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CategoryServiceTest {

    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addCategory() {
        //when
        categoryService.addCategory("test","testParent","category");

        //then
        categoryService.isValidName("test","category");


    }

    @Test
    void updateCategory() {
    }

    @Test
    void getCategoryById() {
    }

    @Test
    void getAllChildCategories() {
    }

    @Test
    void getChildCategoriesByName() {
    }

    @Test
    void getParentCategories() {
    }

    @Test
    void getCategoryByName() {
        //when
        categoryService.getCategoryByName("test","category");
        //then
        verify(categoryRepository).findByName("test");
    }

    @Test
    void getCategoryByNameBot() {
    }

    @Test
    void getParentCategoryList() {
    }

    @Test
    void getAllCategoriesByParentId() {
    }

    @Test
    void getAllCategories() {
    }

    @Test
    void isValidNameTest() {
        //when
        categoryService.isValidName("test","category");

        //then
        verify(categoryRepository).findByName("test");
    }

    @Test
    void save() {
    }

    @Test
    void updateParent() {
    }
}