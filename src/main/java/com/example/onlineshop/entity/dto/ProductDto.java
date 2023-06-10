package com.example.onlineshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto {
    private String name;
    private Double price;
    private int amount;
    private String param;
    private String category;
}
