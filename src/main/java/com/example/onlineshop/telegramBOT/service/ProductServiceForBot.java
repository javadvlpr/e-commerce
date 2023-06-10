package com.example.onlineshop.telegramBOT.service;

import com.example.onlineshop.entity.ProductEntity;
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
public class ProductServiceForBot {
    private final ProductService productService;
    public SendMessage getProducts(Long chatId, String categoryName) {
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                "|\uD83E\uDEF5 Choose product");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        int i = 0;
        KeyboardRow row = new KeyboardRow();
        for (ProductEntity p : productService.getProductsByCategoryName(categoryName)) {
            if(p.getAmount()>0) {
                if (i < 2) {
                    row.add(p.getName());
                    i++;
                } else {
                    rows.add(row);
                    row = new KeyboardRow();
                    row.add(p.getName());
                    i = 1;
                }
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
    public SendMessage getProduct(String name, Long chatId, int amount,boolean inOrder,boolean inHistory) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        for (ProductEntity product : productService.getAll()) {
            if (Objects.equals(product.getName(), name)) {
                String productInfo = "| Name : " + product.getName() + "\n"
                        + "| Param: " + product.getParam() + "\n"
                        + "| Price: " + product.getPrice() + "\n"
                        + "| Amount: " + product.getAmount() + "\n"
                        + "| Min : 1" + "\n"
                        + "| Max " + product.getAmount();
                String forHistory = "| Name : "+product.getName()+"\n"
                        +"| Param : " + product.getParam()+"\n"
                        +"| Price : "+product.getPrice()+amount+"\n"
                        +"| Amount :"+amount;
                InlineKeyboardButton increaseButton = new InlineKeyboardButton("+");
                increaseButton.setCallbackData("+");

                InlineKeyboardButton amountButton = new InlineKeyboardButton(String.valueOf(amount));
                amountButton.setCallbackData("amount");

                InlineKeyboardButton decreaseButton = new InlineKeyboardButton("-");
                decreaseButton.setCallbackData("-");

                InlineKeyboardButton confirmButton = new InlineKeyboardButton("✅");
                confirmButton.setCallbackData("✅");

                InlineKeyboardButton deleteButton = new InlineKeyboardButton("Delete");
                deleteButton.setCallbackData("Delete");

                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(decreaseButton);
                row1.add(amountButton);
                row1.add(increaseButton);

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(confirmButton);

                if(inOrder){
                    List<InlineKeyboardButton> row3 = new ArrayList<>();
                    row3.add(deleteButton);
                    keyboard.add(row1);
                    keyboard.add(row2);
                    keyboard.add(row3);
                }
                else {
                    keyboard.add(row1);
                    keyboard.add(row2);
                }
                if(inHistory){
                    sendMessage.setText(forHistory);
                }else {
                    sendMessage.setText(productInfo);
                }
                break;
            }
        }

        markup.setKeyboard(keyboard);

        if(!inHistory) {
            sendMessage.setReplyMarkup(markup);
        }

        return sendMessage;
    }
}
