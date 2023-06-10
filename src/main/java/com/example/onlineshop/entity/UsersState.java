package com.example.onlineshop.entity;

import com.example.onlineshop.enums.UserState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity(name = "states")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersState extends BaseEntity{
    private Long userId;
    @Enumerated(EnumType.STRING)
    private UserState state;

}
