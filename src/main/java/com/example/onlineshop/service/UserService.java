package com.example.onlineshop.service;

import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.exceptions.AlreadyExistException;
import com.example.onlineshop.exceptions.DataNotFoundException;
import com.example.onlineshop.exceptions.NotAllowedPageException;
import com.example.onlineshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity validUser(String userName, String password, String path) {
        Optional<UserEntity> user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
            if (user.get().getRoles().contains(Roles.ADMIN) || user.get().getRoles().contains(Roles.SUPER_ADMIN)) {
                if (user.get().isDeleted()) {
                    throw new NotAllowedPageException("user was blocked ." + path);
                } else if (passwordEncoder.matches(password, user.get().getPassword())) {
                    return user.get();
                }
            }
        }
        throw new DataNotFoundException("username or password is wrong ." + path);
    }

    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public Optional<UserEntity> getByChatId(Long id) {
        return userRepository.findByChatId(id);
    }

    public UserEntity getUserByName(String username, String path) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new DataNotFoundException("user not found ." + path);
    }

    public void isExistUserName(String userName, String path) {
        if (userRepository.findByUsername(userName).isPresent()) {
            throw new AlreadyExistException("username is already exists : " + userName + "." + path);
        }
    }

    public void addAdmin(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean hasPermission(Permissions permissions, List<Permissions> permissionsList, String path) {
        for (Permissions permissions1 : permissionsList) {
            if (permissions1.equals(permissions)) {
                return true;
            }
        }
        throw new NotAllowedPageException("you have no permission to this method ." + path);
    }

    public List<UserEntity> getAdmins() {
        return getByRole(userRepository.findAll(), Roles.ADMIN);
    }

    private List<UserEntity> getByRole(List<UserEntity> userEntities, Roles role) {
        List<UserEntity> users = new ArrayList<>();
        boolean isValid;
        for (UserEntity user : userEntities) {
            isValid = false;
            for (int i = 0; i < user.getRoles().size(); i++) {
                if (Objects.equals(user.getRoles().get(i), role) && !user.isDeleted()) {
                    isValid = true;
                    break;
                }
            }
            if (isValid && !user.isDeleted()) {
                users.add(user);
            }
        }
        return users;
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("user not found"));
    }

    public boolean updateAdminPermissions(Long id, List<Permissions> permissions) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().getPermissions().clear();
            user.get().getPermissions().addAll(permissions);
            userRepository.save(user.get());
            return true;
        }
        throw new DataNotFoundException("user not found");
    }

    public List<Permissions> permissionConverter(List<String> permissionsList) {
        List<Permissions> permissions = new ArrayList<>();
        for (String permission : permissionsList) {
            switch (permission) {
                case "ADD" -> permissions.add(Permissions.ADD);
                case "EDIT" -> permissions.add(Permissions.EDIT);
                case "DELETE" -> permissions.add(Permissions.DELETE);
            }
        }
        return permissions;
    }

    @Transactional
    public void updateState(UserEntity user) {
        userRepository.save(user);
    }

    public void blockUser(UserEntity user) {
        user.setDeleted(true);
        userRepository.save(user);
    }
}
