package com.example.onlineshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity(name = "history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HistoryEntity extends BaseEntity {
    @OneToOne
    private OrderEntity orderEntity;
}
