package com.example.onlineshop.telegramBOT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BotService {
    public SendMessage requestContact(Long chatId) {

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "| \uD83D\uDE0A Welcome\n| \uD83D\uDCDE Sand your number");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("\uD83D\uDCDE Share number");
        button.setRequestContact(true);
        row.add(button);

        rows.add(row);

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
    public SendMessage menu(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDECD Shop");
        row.add("\uD83D\uDED2 Orders");
        rows.add(row);

        row = new KeyboardRow();
        row.add("\uD83D\uDCC3 History");
        rows.add(row);

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
    public SendMessage registered(Long chatId) {
        return new SendMessage(chatId.toString(), "|✅ Registered");
    }
    public SendMessage wrongCommand(Long chatId) {
        return new SendMessage(chatId.toString(), "|⛔️ Wrong command");
    }
}
