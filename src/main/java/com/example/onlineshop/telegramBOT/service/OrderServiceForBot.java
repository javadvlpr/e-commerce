package com.example.onlineshop.telegramBOT.service;

import com.example.onlineshop.entity.OrderEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.entity.dto.CreateOrderDto;
import com.example.onlineshop.service.OrderService;
import com.example.onlineshop.service.ProductService;
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
public class OrderServiceForBot {
    private final OrderService orderService;
    private final ProductService productService;
    public SendMessage addOrder(Long chatId, String productName, Integer amount, UserEntity user, Long orderId) {
        SendMessage sendMessage = new SendMessage();
        for (ProductEntity p : productService.getAll()) {
            if (Objects.equals(p.getName(), productName)) {
                if(Objects.isNull(orderId)) {
                    if(checkOrderName(user.getId(),productName,amount)){
                        sendMessage.setText("✅");
                        sendMessage.setChatId(chatId);
                        return sendMessage;
                    }
                    else {
                        orderService.createOrder(new CreateOrderDto(user.getId(), p.getId(), amount));
                    }
                }
            }
        }
        sendMessage.setText("✅");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
    public SendMessage addOrderAgain(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("➕ Add more");
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
    public SendMessage getOrdersInline(Long chatId, UserEntity user) {
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (OrderEntity o : orderService.getOrdersByOwnerId(user.getId())) {
            for (ProductEntity p : o.getProducts()) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton product = new InlineKeyboardButton(p.getName());
                product.setCallbackData(p.getName()+"*"+o.getAmount()+"*"+o.getId());
                row.add(product);
                rows.add(row);
            }
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setText("| Your products :");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
    public boolean checkOrderName(Long userId,String productName, int amount){
        for (OrderEntity o:orderService.getOrdersByOwnerId(userId)) {
            for (ProductEntity p:o.getProducts()) {
                if(Objects.equals(p.getName(),productName)){
                    orderService.updateOrder(o.getId(), o.getAmount() + amount);
                    return true;
                }
            }
        }
        return false;
    }
}
