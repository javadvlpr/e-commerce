package com.example.onlineshop.controller;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.entity.dto.ProductDto;
import com.example.onlineshop.entity.dto.UpdateCategoryOfProductDto;
import com.example.onlineshop.entity.dto.UpdateProductDto;
import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.service.CategoryService;
import com.example.onlineshop.service.ProductService;
import com.example.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final HomePageController homePageController;
    private final UserService userService;
    private final ModelMapper mapper;

    @GetMapping("/product_list/{page}")
    public String showProductsPage(Model model, @PathVariable Integer page) {
        model.addAttribute("products", productService.getAllProducts(page - 1, model));
        model.addAttribute("chooseProduct", new ProductEntity());
        model.addAttribute("categories", categoryService.getAllChildCategories());
        return "showProducts";
    }

    @PostMapping("/create_product")
    public String createProduct(@ModelAttribute ProductDto productDto, Principal principal, Model model) {
        ProductEntity product = ProductEntity.map(productDto);
        productService.createProduct(product, "product");
        return homePageController.productPage(principal, model);
    }

    @PostMapping("/update_product")
    public String updateProduct(UpdateProductDto productDto, Model model, Principal principal) {
        hasPermission(Permissions.EDIT, principal.getName());
        CategoriesEntity category = categoryService.getCategoryByName(productDto.getCategory(), "product");
        productService.updateProduct(ProductEntity.builder()
                .param(productDto.getParam())
                .name(productDto.getName())
                .amount(productDto.getAmount())
                .price(productDto.getPrice())
                .categories(category)
                .build(), productDto.getId(),"product");
        return homePageController.productPage(principal, model);
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model, Principal principal) {
        hasPermission(Permissions.DELETE, principal.getName());
        productService.deleteProduct(id);
        return homePageController.productPage(principal, model);
    }

    public boolean hasPermission(Permissions permissions, String userName) {
        UserEntity user = userService.getUserByName(userName, "product");
        if (!user.getRoles().contains(Roles.SUPER_ADMIN)) {
            return userService.hasPermission(permissions, user.getPermissions(), "product");
        }
        return true;
    }


}
