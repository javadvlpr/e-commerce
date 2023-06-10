package com.example.onlineshop.repository;

import com.example.onlineshop.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    @Query(value = "select * from history where id = ?1 and is_deleted = false ", nativeQuery = true)
    Optional<HistoryEntity> findById(Long id);

    @Query(value = "select h.* from history h join orders o on h.order_entity_id = o.id where o.user_id = ?1", nativeQuery = true)
    List<HistoryEntity> findByUserId(Long id);
}
