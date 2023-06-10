package com.example.onlineshop.entity;

import com.example.onlineshop.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.List;

@Entity(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderEntity extends BaseEntity{
    @ManyToOne
    private UserEntity user;
    private Double price;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ProductEntity> products;
    private OrderStatus orderStatus;
    private int amount;
}
