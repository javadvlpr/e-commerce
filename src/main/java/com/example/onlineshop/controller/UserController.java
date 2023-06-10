package com.example.onlineshop.controller;

import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.entity.dto.CreateAdminDto;
import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {
    private final HomePageController homePageController;
    private final UserService userService;
    private final ModelMapper mapper;

    @PostMapping("/create_admin")
    private String addAdmin(@ModelAttribute CreateAdminDto createAdminDto, Principal principal, Model model) {
        userService.isExistUserName(createAdminDto.getUsername(), "user");
        userService.addAdmin(mapper.map(createAdminDto, UserEntity.class));
        return homePageController.userPage(principal, model);
    }

    @PostMapping("/update_admin/{id}")
    public String updateAdmin(@PathVariable Long id, @RequestParam List<String> permissions, Model model, Principal principal) {
        List<Permissions> permissionsList = userService.permissionConverter(permissions);
        userService.updateAdminPermissions(id, permissionsList);
        model.addAttribute("users", userService.getAdmins());
        return homePageController.userPage(principal, model);
    }

    @GetMapping("/block_admin/{id}")
    public String blockAdminPage(@PathVariable Long id, Principal principal, Model model) {
        hasPermission(Permissions.DELETE, principal.getName());
        userService.blockUser(userService.getUserById(id));
        model.addAttribute("users", userService.getAdmins());
        return homePageController.userPage(principal, model);
    }

    @GetMapping("/admins")
    public String adminsPage(Model model, @PathVariable Integer page) {
        model.addAttribute("users", userService.getAdmins());
        model.addAttribute("chooseUser",new UserEntity());
        return "usersList";
    }
    public boolean hasPermission(Permissions permissions, String userName) {
        UserEntity user = userService.getUserByName(userName, "user");
        if (!user.getRoles().contains(Roles.SUPER_ADMIN)) {
            return userService.hasPermission(permissions, user.getPermissions(), "user");
        }
        return true;
    }
}
