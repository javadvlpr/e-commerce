package com.example.onlineshop.repository;

import com.example.onlineshop.entity.CategoriesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    CategoriesEntity parent = CategoriesEntity.builder()
            .parentCategory(null)
            .name("parent")
            .hasChild(true)
            .build();
    CategoriesEntity child = CategoriesEntity.builder()
            .parentCategory(parent)
            .name("olma")
            .hasChild(false)
            .build();

    @Test
    void findByNameTest() {
        parent.setId(1L);
        child.setId(2L);
        save(parent);
        save(child);
        Optional<CategoriesEntity> category = categoryRepository.findByName(child.getName());
        assertTrue(category.isPresent());
    }

    @Test
    void findById() {
        parent.setId(1L);
        child.setId(2L);
        save(parent);
        save(child);
        Optional<CategoriesEntity> category = categoryRepository.findById(1L);
        assertTrue(category.isPresent());
    }

    @Test
    void getByParentCategory() {
        save(parent);
        List<CategoriesEntity> byParentCategory = categoryRepository.getByParentCategory();
        assertFalse(byParentCategory.isEmpty());
    }

    @Test
    void findAllByParentCategoryId() {
        parent.setId(1L);
        child.setId(2L);
        save(parent);
        save(child);
        List<CategoriesEntity> categories = categoryRepository.findAllByParentCategoryId(parent.getId());
        assertTrue(categories.size()>0);
    }

    @Test
    void findAll() {
        parent.setId(1L);
        child.setId(2L);
        save(parent);
        save(child);
        List<CategoriesEntity> categories = categoryRepository.findAll();
        assertTrue(categories.size()>0);
    }


    void save(CategoriesEntity category){
        category.setDeleted(false);
        categoryRepository.save(category);
    }
}