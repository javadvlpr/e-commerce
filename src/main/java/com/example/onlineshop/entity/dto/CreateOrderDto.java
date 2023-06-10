package com.example.onlineshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateOrderDto {
    private Long userId;
    private Long productId;
    private int amount;
}
