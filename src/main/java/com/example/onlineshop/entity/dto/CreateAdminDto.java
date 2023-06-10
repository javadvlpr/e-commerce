package com.example.onlineshop.entity.dto;

import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateAdminDto {
    private String name;
    private String password;
    private String username;
    private List<Roles> roles;
    private List<String> permissions;
}
