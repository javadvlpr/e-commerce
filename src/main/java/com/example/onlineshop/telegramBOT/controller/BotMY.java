package com.example.onlineshop.telegramBOT.controller;

import com.example.onlineshop.entity.CategoriesEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.enums.Permissions;
import com.example.onlineshop.enums.Roles;
import com.example.onlineshop.enums.UserState;
import com.example.onlineshop.service.CategoryService;
import com.example.onlineshop.service.OrderService;
import com.example.onlineshop.service.ProductService;
import com.example.onlineshop.service.UserService;
import com.example.onlineshop.telegramBOT.payload.Info;
import com.example.onlineshop.telegramBOT.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class BotMY extends TelegramLongPollingBot implements Info {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BotService botService;
    private final OrderServiceForBot orderServiceForBot;
    private final OrderService orderService;
    private final HistoryServiceForBot historyServiceForBot;
    private final ProductServiceForBot productServiceForBot;
    private final CategoryServiceForBot categoryServiceForBot;
    private static Map<Long, String> product = new HashMap<>();
    private static Map<Long, Integer> amount = new HashMap<>();
    private static Map<Long, Long> orderId = new HashMap<>();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    @Async
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                Optional<UserEntity> currentUser = userService.getByChatId(update.getMessage().getChatId());
                UserState userState = null;
                Message message = update.getMessage();
                String text = message.getText();
                Long chatId = message.getChatId();

                if (currentUser.isEmpty()) {
                    userState = UserState.SHARE_NUMBER;
                }

                if (currentUser.isEmpty() && message.hasContact()) {
                    userState = UserState.SHARED_NUMBER;
                }

                if (currentUser.isPresent()) {
                    if (Objects.equals(text, "\uD83D\uDECD Shop") ||
                            Objects.equals(text, "➕ Add more")) {
                        userState = UserState.SHOP;
                    }

                    if (Objects.equals(text, "\uD83D\uDED2 Orders")) {
                        userState = UserState.SHOW_ORDERS;
                    }

                    if (Objects.equals(text, "\uD83D\uDCC3 History")) {
                        userState = UserState.HISTORY;
                    }
                    if (Objects.equals(text, "✅ Accepted")) {
                        userState = UserState.HISTORY_ACCEPT;
                    }
                    if (Objects.equals(text, "❌ Rejected")) {
                        userState = UserState.HISTORY_REJECT;
                    }

                    if (Objects.equals(text, "⬅️ Back")) {
                        switch (currentUser.get().getState()) {
                            case IN_SHOP -> userState = UserState.SHOP;

                            default -> userState = UserState.MENU;
                        }
                    }

                    if (!(Objects.equals(text, "⬅️ Back"))) {
                        switch (currentUser.get().getState()) {
                            case SHOP -> {
                                for (CategoriesEntity c : categoryService.getAllCategories()) {
                                    if (Objects.equals(text, c.getName())) {
                                        userState = UserState.IN_SHOP;
                                        amount.remove(chatId);
                                        product.remove(chatId);
                                        orderId.remove(chatId);
                                    }
                                }
                            }
                            case IN_SHOP -> {
                                if (categoryService.getChildCategoriesByName(text).isEmpty()) {
                                    userState = UserState.PRODUCT;
                                } else {
                                    for (CategoriesEntity c : categoryService.getAllCategories()) {
                                        if (Objects.equals(text, c.getName())) {
                                            userState = UserState.IN_SHOP;
                                        }
                                    }
                                }
                            }
                            case ORDERS -> {
                                for (ProductEntity p : productService.getAll()) {
                                    if (Objects.equals(p.getName(), text)) {
                                        product.put(chatId, text);
                                        userState = UserState.ORDERS;
                                    }
                                }
                            }
                        }
                    }
                }

                if (userState == null) {
                    try {
                        execute(botService.wrongCommand(chatId));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else if (userState != null) {
                    userSwitch(userState,chatId,message,currentUser);
                }
            });
            executor.shutdown();
        }
        else if (update.hasCallbackQuery()) {
            userInline(update);
        }
    }

    public UserEntity createFromContact(Message message) {
        Contact contact = message.getContact();
        UserEntity user = UserEntity.builder()
                .chatId(message.getChatId())
                .permissions(List.of(Permissions.USER))
                .roles(List.of(Roles.USER))
                .name(contact.getFirstName())
                .password(passwordEncoder.encode("12345678"))
                .username(contact.getPhoneNumber())
                .state(UserState.valueOf(UserState.REGISTERED.toString()))
                .build();
        return user;
    }
    public void userSwitch(UserState userState, Long chatId, Message message, Optional<UserEntity> currentUser) {
        String text = message.getText();
        switch (userState) {
            case SHARE_NUMBER -> {
                try {
                    execute(botService.requestContact(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case SHARED_NUMBER -> {
                UserEntity userCreate = createFromContact(message);
                userService.save(userCreate);
                try {
                    execute(botService.registered(chatId));
                    execute(botService.menu(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case SHOP -> {
                try {
                    execute(categoryServiceForBot.parentCategories(chatId));
                    currentUser.get().setState(UserState.valueOf(UserState.SHOP.toString()));
                    userService.updateState(currentUser.get());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case IN_SHOP -> {
                try {
                    execute(categoryServiceForBot.childCategories(chatId, text));
                    currentUser.get().setState(UserState.valueOf(UserState.IN_SHOP.toString()));
                    userService.updateState(currentUser.get());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case PRODUCT -> {
                try {
                    execute(productServiceForBot.getProducts(chatId, text));
                    currentUser.get().setState(UserState.valueOf(UserState.ORDERS.toString()));
                    userService.updateState(currentUser.get());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ORDERS -> {
                try {
                    if (amount.get(chatId) != null) {
                        execute(productServiceForBot.getProduct(text, chatId, amount.get(chatId), false, false));
                        currentUser.get().setState(UserState.valueOf(UserState.ADD_ORDER.toString()));
                        userService.updateState(currentUser.get());
                    } else {
                        execute(productServiceForBot.getProduct(text, chatId, 0, false, false));
                        currentUser.get().setState(UserState.valueOf(UserState.ADD_ORDER.toString()));
                        userService.updateState(currentUser.get());
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case MENU -> {
                try {
                    execute(botService.menu(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ADD_ORDER -> {
                System.err.println("hi");
            }
            case SHOW_ORDERS -> {
                try {
                    execute(orderServiceForBot.getOrdersInline(chatId, currentUser.get()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case HISTORY -> {
                try {
                    execute(historyServiceForBot.historyMenu(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case HISTORY_ACCEPT -> {
                try {
                    execute(historyServiceForBot.getHistory(chatId, currentUser.get(), false));
                    currentUser.get().setState(UserState.valueOf(UserState.HISTORY_ACCEPT.toString()));
                    userService.updateState(currentUser.get());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case HISTORY_REJECT -> {
                try {
                    execute(historyServiceForBot.getHistory(chatId, currentUser.get(), true));
                    currentUser.get().setState(UserState.valueOf(UserState.HISTORY_REJECT.toString()));
                    userService.updateState(currentUser.get());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void userInline(Update update){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Long chatId = callbackQuery.getFrom().getId();
            Optional<UserEntity> currentUser = userService.getByChatId(chatId);
            if (Objects.equals(callbackData, "-") || (Objects.equals(callbackData, "+"))) {
                if (Objects.equals(callbackData, "+")) {
                    ProductEntity productEntity = null;
                    for (ProductEntity p : productService.getAll()) {
                        if (Objects.equals(p.getName(), product.get(chatId))) {
                            productEntity = p;
                            break;
                        }
                    }

                    if (amount.containsKey(chatId)) {
                        int currentAmount = amount.get(chatId);
                        if (currentAmount < productEntity.getAmount()) {
                            amount.put(chatId, currentAmount + 1);
                        }
                    } else {
                        amount.put(chatId, 1);
                    }
                } else if (Objects.equals(callbackData, "-")) {
                    if (amount.containsKey(chatId)) {
                        int currentAmount = amount.get(chatId);
                        if (currentAmount > 1) {
                            amount.put(chatId, currentAmount - 1);
                        }
                    }
                }
                try {
                    execute(productServiceForBot.getProduct(product.get(chatId), chatId, amount.get(chatId),false,false));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.equals(callbackData, "Delete")) {
                orderService.deleteOrder(orderId.get(chatId),true);
                try {
                    execute(orderServiceForBot.getOrdersInline(chatId,currentUser.get()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.equals(callbackData, "✅")) {
                try {
                    ProductEntity productEntity = null;
                    for (ProductEntity p : productService.getAll()) {
                        if (Objects.equals(p.getName(), product.get(chatId))) {
                            productEntity = p;
                        }
                    }
                    if(productService.updateProductOnBuyProduct(productEntity.getId(), amount.get(chatId))) {
                        execute(orderServiceForBot.addOrder(chatId, product.get(chatId), amount.get(chatId), currentUser.get(),
                                orderId.get(chatId)));
                    }
                    execute(orderServiceForBot.addOrderAgain(chatId));

                    currentUser.get().setState(UserState.valueOf(UserState.FULL_ORDER.toString()));
                    userService.updateState(currentUser.get());
                    amount.remove(chatId);
                    product.remove(chatId);
                    orderId.remove(chatId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String[] parts = callbackData.split("\\*");
                if (parts.length == 3) {
                    String productName = parts[0];
                    int productAmount = Integer.parseInt(parts[1]);
                    Long orderId1 = Long.valueOf(parts[2]);
                    amount.put(chatId,productAmount);
                    product.put(chatId,productName);
                    orderId.put(chatId,orderId1);
                    try {
                        if(Objects.equals(currentUser.get().getState(),UserState.HISTORY_ACCEPT)||
                                Objects.equals(currentUser.get().getState(),UserState.HISTORY_REJECT)) {
                            execute(productServiceForBot.getProduct(productName, chatId, productAmount, true,true));
                        }
                        else {
                            execute(productServiceForBot.getProduct(productName, chatId, productAmount, true,false));
                        }
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        executor.shutdown();
    }
}