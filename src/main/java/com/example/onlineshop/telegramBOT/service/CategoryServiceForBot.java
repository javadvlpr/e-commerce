package com.example.onlineshop.telegramBOT.service;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceForBot {
    private final CategoryService categoryService;
    public SendMessage parentCategories(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose category");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        int i = 0;
        KeyboardRow row = new KeyboardRow();
        for (CategoriesEntity c : categoryService.getParentCategories()) {
            if (i < 2) {
                row.add(c.getName());
                i++;
            } else {
                rows.add(row);
                row = new KeyboardRow();
                row.add(c.getName());
                i = 1;
            }
        }
        if (!row.isEmpty()) {
            row.add("⬅️ Back");
            rows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
    public SendMessage childCategories(Long chatId, String parentName) {
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose category");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        int i = 0;
        KeyboardRow row = new KeyboardRow();
        for (CategoriesEntity c : categoryService.getChildCategoriesByName(parentName)) {
            if (i < 2) {
                row.add(c.getName());
                i++;
            } else {
                rows.add(row);
                row = new KeyboardRow();
                row.add(c.getName());
                i = 1;
            }
        }
        if (!row.isEmpty()) {
            row.add("⬅️ Back");
            rows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
