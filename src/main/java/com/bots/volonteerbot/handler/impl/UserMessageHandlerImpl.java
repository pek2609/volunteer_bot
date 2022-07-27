package com.bots.volonteerbot.handler.impl;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.handler.OrderMessageHandler;
import com.bots.volonteerbot.handler.UserMessageHandler;
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
    private final BotUserService botUserService;

    public UserMessageHandlerImpl(OrderService orderService, OrderMessageHandler orderMessageHandler, OrderCache orderCache, BotUserService botUserService) {
        this.orderService = orderService;
        this.orderMessageHandler = orderMessageHandler;
        this.orderCache = orderCache;
        this.botUserService = botUserService;
    }

    @Override
    public SendMessage choose(Message message) {
        final BotUser botUser = botUserService.findByChatId(message.getChatId());
        final String messageText = message.getText();
        switch (botUser.getState()) {
            case NONE -> {
                if (messageText.equals("/start")) {
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
                    return SendMessage.builder()
                            .chatId(String.valueOf(message.getChatId()))
                            .text("Невідома команда! Щоб розпочати роботу бота, натисніть або напишіть /start")
                            .build();
                }
            }
            case START -> {
                switch (messageText) {
                    case "Допомога" -> {
                        Order order = new Order();
                        order.setOrderState(OrderState.ENTER_NAME);
                        order.setBotUser(botUser);
                        orderCache.add(order, message.getChatId());
                        botUser.setState(State.MAKE_ORDER);
                        return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Як до Вас звертатися волонтерам?").build();
                    }
                    case "Відміна запиту" -> {
                        if (orderService.existByChatId(message.getChatId())) {
                            final Order order = orderService.findByChatId(message.getChatId());
                            orderService.delete(order.getId());
                            orderCache.removeByChatId(message.getChatId());
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Запит видалено!").build();
                        } else {
                            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Жодного запиту немає. Щоб зробити запит, натисніть кнопку \"Допомога\".").build();
                        }
                    }
                    default -> {
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
            default -> throw new UnsupportedOperationException("Unknown status");
        }
    }
}
