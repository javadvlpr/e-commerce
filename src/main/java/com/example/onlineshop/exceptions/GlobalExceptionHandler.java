package com.example.onlineshop.exceptions;

import com.example.onlineshop.controller.HomePageController;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final HomePageController homePageController;

    @org.springframework.web.bind.annotation.ExceptionHandler(DataNotFoundException.class)
    public String dataNotFoundExceptionHandler(DataNotFoundException e,Model model, Principal principal) {
        return handleException(e, model, principal);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AlreadyExistException.class)
    public String alreadyExistExceptionHandler(AlreadyExistException e, Model model,Principal principal) {
        return handleException(e, model, principal);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(NotAllowedPageException.class)
    public String notAllowedPAgeException(NotAllowedPageException e,Model model,Principal principal){
        return handleException(e, model, principal);
    }

    private String handleException(Exception e, Model model, Principal principal) {
        String message = e.getMessage();
        String[] split = message.split("\\.");
        model.addAttribute("exception", split[0]);
        switch (split[1]) {
            case "category":
                return homePageController.categoryPage(principal, model);
            case "product":
                return homePageController.productPage(principal, model);
            case "order":
                return homePageController.orderPage(principal, model);
            case "user":
                return homePageController.userPage(principal, model);
            case "login":
                return homePageController.loginPage();
        }
        return homePageController.menuPage(principal, model);
    }

}