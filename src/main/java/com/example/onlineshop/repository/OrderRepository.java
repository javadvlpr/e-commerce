package com.example.onlineshop.repository;

import com.example.onlineshop.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = "select * from orders where id = ?1 and is_deleted = false ", nativeQuery = true)
    Optional<OrderEntity> getOrderById(Long id);

    @Query(value = "select * from orders where user_id = ?1 and is_deleted = false ", nativeQuery = true)
    List<OrderEntity> getOrdersByOwnerId(Long ownerId);

    @Query(value = "update orders set is_deleted=true where id = ?1", nativeQuery = true)
    void deleteById(Long id);

    @Query(value = "select * from orders where is_deleted = false",nativeQuery = true)
    Page<OrderEntity> findAll(Pageable pageable);
}
