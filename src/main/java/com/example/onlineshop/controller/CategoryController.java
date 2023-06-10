package com.example.onlineshop.controller;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.entity.dto.CreateCategoryDto;
import com.example.onlineshop.entity.dto.UpdateCategoryDto;
import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.service.CategoryService;
import com.example.onlineshop.service.ProductService;
import com.example.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final HomePageController homePageController;
    private final ProductService productService;
    private final UserService userService;

    @PostMapping("/add-category")
    public String addCategory(@ModelAttribute CreateCategoryDto categoryDto, Principal principal, Model model) {
        hasPermission(Permissions.ADD, principal.getName());
        categoryService.addCategory(categoryDto.getName(),categoryDto.getParentName(),"category");
        return homePageController.categoryPage(principal, model);
    }


    @GetMapping("/get-all")
    public String getAllCategories(Model model) {
        List<CategoriesEntity> allCategories = categoryService.getAllCategories();
        model.addAttribute("categories", allCategories);
        return "categoryList";
    }

    @GetMapping("/get-by-id/{id}")
    public String getCategoryById(@PathVariable Long id, Model model, Principal principal) {
        UserEntity user = userService.getUserByName(principal.getName(), "category");
        model.addAttribute("user", user);
        List<CategoriesEntity> categories = categoryService.getAllCategoriesByParentId(id);
        model.addAttribute("categories", categories);
        return "categoryList";
    }


    @PostMapping("/update_category")
    public String updateCategory(UpdateCategoryDto categoryDto, Principal principal, Model model) {
        hasPermission(Permissions.EDIT, principal.getName());
        categoryService.updateCategory(categoryDto.getId(),categoryDto.getName(),categoryDto.getParent(),"category");
        return homePageController.categoryPage(principal, model);
    }

    public boolean hasPermission(Permissions permissions, String userName) {
        UserEntity user = userService.getUserByName(userName, "category");
        if (!user.getRoles().contains(Roles.SUPER_ADMIN)) {
            return userService.hasPermission(permissions, user.getPermissions(), "category");
        }
        return true;
    }
}
