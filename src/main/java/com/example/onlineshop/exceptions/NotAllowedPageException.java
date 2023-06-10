package com.example.onlineshop.exceptions;

public class NotAllowedPageException extends RuntimeException{
    public NotAllowedPageException(String message) {
        super(message);
    }
}
