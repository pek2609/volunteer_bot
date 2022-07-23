package com.bots.volonteerbot.handler.impl;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.exception.EntityNotFoundException;
import com.bots.volonteerbot.handler.OrderMessageHandler;
import com.bots.volonteerbot.handler.UserMessageHandler;
import com.bots.volonteerbot.logger.LoggerLevel;
import com.bots.volonteerbot.logger.LoggerService;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.entity.Order;
import com.bots.volonteerbot.persistence.type.OrderState;
import com.bots.volonteerbot.persistence.type.State;
import com.bots.volonteerbot.service.BotUserService;
import com.bots.volonteerbot.service.OrderService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


@Service
public class UserMessageHandlerImpl implements UserMessageHandler {

    private final OrderService orderService;
    private final OrderMessageHandler orderMessageHandler;
    private final OrderCache orderCache;
    private final LoggerService loggerService;
    private final BotUserService botUserService;

    public UserMessageHandlerImpl(OrderService orderService, OrderMessageHandler orderMessageHandler, OrderCache orderCache, LoggerService loggerService, BotUserService botUserService) {
        this.orderService = orderService;
        this.orderMessageHandler = orderMessageHandler;
        this.orderCache = orderCache;
        this.loggerService = loggerService;
        this.botUserService = botUserService;
    }

    @Override
    public SendMessage choose(Message message) {
        try {
            final BotUser botUser = botUserService.findByChatId(message.getChatId());
            final String messageText = message.getText();
            switch (botUser.getState()) {
                case NONE -> {
                    if (messageText.equals("/start")) {
                        loggerService.commit(LoggerLevel.INFO, "UserMessageHandler, chatId = " + message.getChatId() + ", state = NONE, /start");
                        botUser.setState(State.START);
                        botUserService.update(botUser);
                        return SendMessage.builder()
                                .text("Добрий день! Виберіть, що хочете зробити, натиснувши на кнопку.")
                                .chatId(String.valueOf(message.getChatId()))
                                .replyMarkup(ReplyKeyboardMarkup.builder()
                                        .keyboardRow(new KeyboardRow() {{
                                            add(KeyboardButton.builder()
                                                    .text("Допомога")
                                                    .build());
                                        }})
                                        .keyboardRow(new KeyboardRow() {{
                                            add(KeyboardButton.builder()
                                                    .text("Відміна запиту")
                                                    .build());
                                        }})
                                        .build())
                                .build();
                    } else {
                        loggerService.commit(LoggerLevel.INFO, "UserMessageHandler, chatId = " + message.getChatId() + ", state = NONE, unknown command");
                        return SendMessage.builder()
                                .chatId(String.valueOf(message.getChatId()))
                                .text("Невідома команда! Щоб розпочати роботу бота, натисніть або напишіть /start")
                                .build();
                    }
                }
                case START -> {
                    switch (messageText) {
                        case "Допомога" -> {
                            loggerService.commit(LoggerLevel.INFO, "UserMessageHandler, chatId = " + message.getChatId() + ", state = START, help pressed");
                            Order order = new Order();
                            order.setOrderState(OrderState.ENTER_NAME);
                            order.setBotUser(botUser);
                            orderCache.add(order, message.getChatId());
                            botUser.setState(State.MAKE_ORDER);
                            loggerService.commit(LoggerLevel.INFO, "OrderMessageHandler, chatId = " + message.getChatId() + ", state = MAKE_ORDER, start");
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Як до Вас звертатися волонтерам?").build();
                        }
                        case "Відміна запиту" -> {
                            loggerService.commit(LoggerLevel.WARN, "UserMessageHandler, chatId = " + message.getChatId() + ", state = START, remove last order start");
                            final Order order = orderCache.findByChatId(message.getChatId());
                            if (order == null) {
                                loggerService.commit(LoggerLevel.ERROR, "UserMessageHandler, chatId = " + message.getChatId() + ", message = order is not found");
                                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Жодного запиту немає. Щоб зробити запит, натисніть кнопку \"Допомога\".").build();
                            }
                            orderService.delete(order.getId());
                            orderCache.removeByChatId(message.getChatId());
                            loggerService.commit(LoggerLevel.WARN, "UserMessageHandler, chatId = " + message.getChatId() + ", state = START, remove last order end");
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Запит видалено!").build();
                        }
                        default -> {
                            loggerService.commit(LoggerLevel.INFO, "UserMessageHandler, chatId = " + message.getChatId() + ", state = START, unknown command");
                            return SendMessage.builder()
                                    .chatId(String.valueOf(message.getChatId()))
                                    .text("Невідома команда. Натисніть кнопку \"Допомога\", щоб зробити запит")
                                    .build();
                        }
                    }
                }
                case MAKE_ORDER -> {
                    return orderMessageHandler.choose(message);
                }
            }
        } catch (
                EntityNotFoundException exception) {
            loggerService.commit(LoggerLevel.ERROR, "UserMessageHandler, chatId = " + message.getChatId() + ", message = " + exception.getMessage());
            SendMessage.builder().chatId(String.valueOf(message.getChatId())).text(exception.getMessage()).build();
        }
        return null;
    }

}
