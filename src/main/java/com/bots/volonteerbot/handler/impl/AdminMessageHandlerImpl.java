package com.bots.volonteerbot.handler.impl;

import com.bots.volonteerbot.cache.BotUserCache;
import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.logger.LoggerLevel;
import com.bots.volonteerbot.logger.LoggerService;
import com.bots.volonteerbot.persistence.datatable.DataTableRequest;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.entity.Order;
import com.bots.volonteerbot.handler.AdminMessageHandler;
import com.bots.volonteerbot.persistence.repository.BotUserRepository;
import com.bots.volonteerbot.persistence.repository.OrderRepository;
import com.bots.volonteerbot.persistence.type.State;
import com.bots.volonteerbot.service.OrderService;
import com.bots.volonteerbot.util.ToStringUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminMessageHandlerImpl implements AdminMessageHandler {

    private final BotUserRepository botUserRepository;
    private final BotUserCache botUserCache;
    private final OrderRepository orderRepository;
    private final OrderCache orderCache;
    private final LoggerService loggerService;
    private final OrderService orderService;

    public AdminMessageHandlerImpl(BotUserRepository botUserRepository, BotUserCache botUserCache, OrderRepository orderRepository, OrderCache orderCache, LoggerService loggerService, OrderService orderService) {
        this.botUserRepository = botUserRepository;
        this.botUserCache = botUserCache;
        this.orderRepository = orderRepository;
        this.orderCache = orderCache;
        this.loggerService = loggerService;
        this.orderService = orderService;
    }

    @Override
    public SendMessage choose(Message message) {
        final String messageText = message.getText();
        try {
            final BotUser admin = botUserCache.findByChatId(message.getChatId());
            switch (admin.getState()) {
                case NONE -> {
                    if (messageText.equals("/start")) {
                        loggerService.commit(LoggerLevel.INFO, "AdminMessageHandler, chatId = " + message.getChatId() + ", state = NONE, /start pressed");
                        admin.setState(State.START);
                        botUserRepository.save(admin);
                        return SendMessage.builder()
                                .text("Вибери, що хочеш зробити")
                                .chatId(String.valueOf(message.getChatId()))
                                .replyMarkup(ReplyKeyboardMarkup.builder()
                                        .keyboardRow(new KeyboardRow() {{
                                            add(KeyboardButton.builder()
                                                    .text("Показати усі запити")
                                                    .build());
                                        }})
                                        .keyboardRow(new KeyboardRow() {{
                                            add(KeyboardButton.builder()
                                                    .text("Показати наступний запит за чергою")
                                                    .build());
                                        }}).build())
                                .build();
                    } else {
                        loggerService.commit(LoggerLevel.INFO, "AdminMessageHandler, chatId = " + message.getChatId() + ", state = NONE, unknown command pressed");
                        return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Невідома команда! Напиши або натисни /start").build();
                    }
                }
                case START -> {
                    switch (messageText) {
                        case "Показати усі запити" -> {
                            loggerService.commit(LoggerLevel.INFO, "AdminMessageHandler, chatId = " + message.getChatId() + ", state = START, show all orders");
                            DataTableRequest dataTableRequest = DataTableRequest.fromPage(orderRepository.count(), 0);
                            List<Order> orders = orderService.findAll(dataTableRequest);
                            String ordersMessageText = ToStringUtil.getAllOrdersToString(orders, dataTableRequest);
                            return SendMessage.builder()
                                    .parseMode("HTML")
                                    .chatId(String.valueOf(message.getChatId()))
                                    .text(ordersMessageText)
                                    .replyMarkup(ToStringUtil.getKeyboard(dataTableRequest)).build();
                        }
                        case "Показати наступний запит за чергою" -> {
                            loggerService.commit(LoggerLevel.INFO, "AdminMessageHandler, chatId = " + message.getChatId() + ", state = START, show next order");
                            final Optional<Order> curOrder = orderRepository.getMinOrderByCreated();
                            if (curOrder.isPresent()) {
                                orderCache.add(curOrder.get(), message.getChatId());
                                botUserRepository.save(admin);
                                return SendMessage.builder()
                                        .parseMode("HTML")
                                        .chatId(String.valueOf(message.getChatId()))
                                        .text(curOrder.get().toString())
                                        .replyMarkup(InlineKeyboardMarkup.builder()
                                                .keyboardRow(List.of(
                                                        InlineKeyboardButton.builder().text("Виконано").callbackData("/done").
                                                                build()
                                                ))
                                                .build())
                                        .build();
                            }
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Активних запитів немає").build();
                        }
                        default -> {
                            loggerService.commit(LoggerLevel.INFO, "AdminMessageHandler, chatId = " + message.getChatId() + ", state = START, unknown command");
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Невідома команда! Натисни одну із кнопок").build();
                        }
                    }
                }
            }
        } catch (EntityNotFoundException e) {
            loggerService.commit(LoggerLevel.ERROR, "AdminMessageHandler, chatId = " + message.getChatId() + ", message = " + e.getMessage());
            SendMessage.builder().chatId(String.valueOf(message.getChatId())).text(e.getMessage()).build();
        }
        return null;
    }
}
