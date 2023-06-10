package com.example.onlineshop.service;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.exceptions.AlreadyExistException;
import com.example.onlineshop.exceptions.DataNotFoundException;
import com.example.onlineshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public void addCategory(String categoryName,String parentName,String path) {
        isValidName(categoryName.toUpperCase(), path);
        if (!Objects.equals(parentName, "no")) {
            CategoriesEntity parentCategory = getCategoryByName(parentName.toUpperCase(), path);
            parentCategory.setHasChild(true);
            save(parentCategory);
            save(CategoriesEntity
                    .builder()
                    .parentCategory(parentCategory)
                    .name(categoryName.toUpperCase())
                    .hasChild(false).build());
        } else {
            save(CategoriesEntity.builder().name(categoryName.toUpperCase()).hasChild(false).build());
        }
    }

    public void updateCategory(Long id,String categoryName,String parentName,String path){
        CategoriesEntity category = getCategoryById(id, path);
        if (!Objects.equals(category.getName(), categoryName.toUpperCase())) {
            isValidName(categoryName.toUpperCase(), path);
            if (parentName == null) {
                category.setParentCategory(null);
                category.setName(categoryName.toUpperCase());
                save(category);
            } else {
                CategoriesEntity parentCategory = getCategoryByName(parentName.toUpperCase(), path);
                category.setParentCategory(parentCategory);
                save(category);
            }
        }
    }

    public CategoriesEntity getCategoryById(Long id, String path) {
        Optional<CategoriesEntity> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new DataNotFoundException("category not found with id: " + id + "." + path);
        }
        return category.get();
    }

    public List<CategoriesEntity> getAllChildCategories() {
        List<CategoriesEntity> categoryList = new ArrayList<>();
        for (CategoriesEntity category : categoryRepository.findAll()) {
            if (category.getParentCategory() != null && !category.isHasChild()) {
                categoryList.add(category);
            }
        }
        return categoryList;
    }

    public List<CategoriesEntity> getChildCategoriesByName(String name) {
        Optional<CategoriesEntity> category = categoryRepository.findByName(name);
        if (category.isEmpty()) {
            return new ArrayList<>();
        }
        return categoryRepository.findAllByParentCategoryId(category.get().getId());
    }

    public List<CategoriesEntity> getParentCategories() {
        return categoryRepository.getByParentCategory();
    }

    public CategoriesEntity getCategoryByName(String categoryName, String path) {
        Optional<CategoriesEntity> category = categoryRepository.findByName(categoryName);
        if (category.isPresent()) {
            return category.get();
        }
        throw new DataNotFoundException("category not found by name : " + categoryName + "." + path);
    }

    public Optional<CategoriesEntity> getCategoryByNameBot(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }

    public List<CategoriesEntity> getAllCategoriesByParentId(Long id) {
        if (id >= 0) {
            return categoryRepository.findAllByParentCategoryId(id);
        }
        return categoryRepository.getByParentCategory();
    }

    public List<CategoriesEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public boolean isValidName(String name, String path) {
        if (categoryRepository.findByName(name).isEmpty()) {
            return true;
        } else {
            throw new AlreadyExistException("There is category with this name ." + path);
        }
    }

    public void save(CategoriesEntity category) {
        categoryRepository.save(category);
    }

}

