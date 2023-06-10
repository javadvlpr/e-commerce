package com.example.onlineshop.repository;

import com.example.onlineshop.entity.CategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoriesEntity,Long> {
    @Query(value = "select * from categories where name=?1 and is_deleted=false",nativeQuery = true)
    Optional<CategoriesEntity> findByName(String name);
    @Query(value = "select * from categories where id=?1 and is_deleted=false",nativeQuery = true)
    Optional<CategoriesEntity> findById(Long id);
    @Query(value = "select * from categories where parent_category_id IS null and is_deleted = false",nativeQuery = true)
    List<CategoriesEntity> getByParentCategory();
    @Query(value = "select * from categories where parent_category_id=?1 and is_deleted=false",nativeQuery = true)
    List<CategoriesEntity> findAllByParentCategoryId(Long id);

    @Query(value = "select * from categories where is_deleted=false",nativeQuery = true)
    List<CategoriesEntity> findAll();
}
