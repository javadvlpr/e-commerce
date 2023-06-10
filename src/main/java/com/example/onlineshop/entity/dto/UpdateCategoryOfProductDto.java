package com.example.onlineshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCategoryOfProductDto {
    private Long id;
    private String name;
    private String category;
}
