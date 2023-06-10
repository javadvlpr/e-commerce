package com.example.onlineshop.controller;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.service.CategoryService;
import com.example.onlineshop.service.OrderService;
import com.example.onlineshop.service.ProductService;
import com.example.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@RequestMapping
@Controller
@RequiredArgsConstructor
public class HomePageController {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping("/menu")
    public String menuPage(Principal principal, Model model) {
        UserEntity user = userService.getUserByName(principal.getName(),"menu");
        model.addAttribute("user", user);
        return "menu";
    }

    @GetMapping("/categories")
    public String categoryPage(Principal principal, Model model) {
        UserEntity user = userService.getUserByName(principal.getName(),"category");
        if (user.getRoles().contains(Roles.ADMIN) || user.getRoles().contains(Roles.SUPER_ADMIN)) {
            model.addAttribute("user", user);
            List<CategoriesEntity> categories = categoryService.getAllCategoriesByParentId(-1L);
            model.addAttribute("categories", categories);
            return "categoryList";
        }
        return "menu";
    }

    @GetMapping("/products")
    public String productPage(Principal principal, Model model) {
        UserEntity user = userService.getUserByName(principal.getName(), "product");
        if (user.getRoles().contains(Roles.ADMIN) || user.getRoles().contains(Roles.SUPER_ADMIN)) {
            model.addAttribute("user", user);
            model.addAttribute("products", productService.getAllProducts(0, model));
            model.addAttribute("categories", categoryService.getAllChildCategories());
            return "showProducts";
        }
        return "menu";
    }

    @GetMapping("/users")
    public String userPage(Principal principal, Model model) {
        UserEntity user = userService.getUserByName(principal.getName(),"user");
        List<UserEntity> users = userService.getAdmins();
        if (user.getRoles().contains(Roles.ADMIN) || user.getRoles().contains(Roles.SUPER_ADMIN)) {
            users.remove(user);
            model.addAttribute("user", user);
            model.addAttribute("users", users);
            model.addAttribute("chooseUser", new UserEntity());
            return "usersList";
        }
        return "menu";
    }

    @GetMapping("/orders")
    public String orderPage(Principal principal, Model model) {
        UserEntity user = userService.getUserByName(principal.getName(),"order");
        if (user.getRoles().contains(Roles.ADMIN) || user.getRoles().contains(Roles.SUPER_ADMIN)) {
            model.addAttribute("user", user);
            model.addAttribute("orders", orderService.getAllOrders(0, model));
            return "ordersList";
        }
        return "menu";
    }
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    public String refactorPage(String path, Model model) {
        String[] s = path.split("\\.");
        model.addAttribute("exception", s[0]);
        return s[1];
    }
}
