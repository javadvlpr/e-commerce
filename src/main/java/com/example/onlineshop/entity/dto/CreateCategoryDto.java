package com.example.onlineshop.entity.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCategoryDto {
    private String name;
    private String parentName;
}
