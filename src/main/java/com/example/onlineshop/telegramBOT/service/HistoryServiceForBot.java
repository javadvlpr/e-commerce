package com.example.onlineshop.telegramBOT.service;

import com.example.onlineshop.entity.HistoryEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.enums.OrderStatus;
import com.example.onlineshop.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class HistoryServiceForBot {
    private final HistoryService historyService;
    public SendMessage getHistory(Long chatId, UserEntity user, boolean isRejected){
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (HistoryEntity h : historyService.getHistoryByUserId(user.getId())) {
            for (ProductEntity p : h.getOrderEntity().getProducts()) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                if(isRejected){
                    if(Objects.equals(h.getOrderEntity().getOrderStatus(), OrderStatus.REJECTED)){
                        InlineKeyboardButton product = new InlineKeyboardButton(p.getName());
                        product.setCallbackData(p.getName()+"*"+h.getOrderEntity().getAmount()+"*"+h.getOrderEntity().getId());
                        row.add(product);
                        rows.add(row);
                    }
                }
                else {
                    InlineKeyboardButton product = new InlineKeyboardButton(p.getName());
                    product.setCallbackData(p.getName() + "*" + h.getOrderEntity().getAmount() + "*" + h.getOrderEntity().getId());
                    row.add(product);
                    rows.add(row);
                }
            }
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setText("| Your history :");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
    public SendMessage historyMenu(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("✅ Accepted");
        row.add("❌ Rejected");
        rows.add(row);

        row = new KeyboardRow();
        row.add("⬅️ Back");
        rows.add(row);

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
