package com.example.onlineshop.repository;

import com.example.onlineshop.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "select * from products where id =?1 and is_deleted = false ", nativeQuery = true)
    ProductEntity getById(Long id);

    @Query(value = "select * from products where categories_id = ?1 and is_deleted = false ", nativeQuery = true)
    List<ProductEntity> getProductsByCategoryId(Long categoryId);

    @Query(value = "select * from products where name =?1 and is_deleted = false ", nativeQuery = true)
    Optional<ProductEntity> getByName(String productName);

    @Query(value = "select p.* from products p join orders_products op on op.products_id = ?1 join orders o on o.id = op.orders_id where p.id=?1 and o.is_deleted=false", nativeQuery = true)
    Optional<ProductEntity> checkProductIsOrdered(Long id);

    @Query(value = "select * from products where id =?1 and is_deleted = false ", nativeQuery = true)
    Optional<ProductEntity> findById(Long id);

    @Query(value = "select * from products where is_deleted = false ", nativeQuery = true)
    Page<ProductEntity> findAll(Pageable pageable);

    @Transactional
    @jakarta.transaction.Transactional
    @Modifying
    @Query(value = "update  products set amount = ?1 where is_deleted = false and id = ?2 ", nativeQuery = true)
    void updateMY(int amount,Long id);
}
