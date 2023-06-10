package com.example.onlineshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoriesEntity extends BaseEntity{
    @Column(unique = true)
    private String name;
    @ManyToOne
    private CategoriesEntity parentCategory;
    private boolean hasChild;
}
