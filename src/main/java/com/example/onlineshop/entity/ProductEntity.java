package com.example.onlineshop.entity;

import com.example.onlineshop.entity.dto.ProductDto;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductEntity extends BaseEntity{
    private String name;
    private Double price;
    private int amount;
    private String param;
    @ManyToOne
    private CategoriesEntity categories;

    public static ProductEntity map(ProductDto productDto){
       return ProductEntity.builder()
                .amount(productDto.getAmount())
                .name(productDto.getName().toUpperCase())
                .param(productDto.getParam())
                .price(productDto.getPrice())
                .build();
    }
}
