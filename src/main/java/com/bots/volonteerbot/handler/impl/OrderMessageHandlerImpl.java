package com.bots.volonteerbot.handler.impl;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.handler.OrderMessageHandler;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.entity.Order;
import com.bots.volonteerbot.persistence.type.OrderState;
import com.bots.volonteerbot.persistence.type.State;
import com.bots.volonteerbot.service.BotUserService;
import com.bots.volonteerbot.service.OrderService;
import com.bots.volonteerbot.util.DataValidUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class OrderMessageHandlerImpl implements OrderMessageHandler {

    private final OrderCache orderCache;
    private final OrderService orderService;
    private final BotUserService botUserService;

    public OrderMessageHandlerImpl(OrderCache orderCache, OrderService orderService, BotUserService botUserService) {
        this.orderCache = orderCache;
        this.orderService = orderService;
        this.botUserService = botUserService;
    }

    @Override
    public SendMessage choose(Message message) {
        final String messageText = message.getText();
        Order order = orderService.findByChatId(message.getChatId());
        BotUser botUser = order.getBotUser();
        if (messageText.equals("Відміна запиту")) {
            orderCache.removeByChatId(message.getChatId());
            botUser.setState(State.START);
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Запит скасовано!").build();
        }
        switch (order.getOrderState()) {
            case ENTER_NAME -> {
                if (!DataValidUtil.validName(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Ім'я введено неправильно! Дозволена лише латиниця та кирилиця, доступна кількість символів від 2 до 50.").build();
                }
                order.setName(messageText);
                order.setOrderState(OrderState.ENTER_AGE);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Вкажіть свій вік").build();
            }
            case ENTER_AGE -> {
                if (!DataValidUtil.validAge(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Введіть вік цифрою від 1 до 120.").build();
                }
                order.setAge(Integer.parseInt(messageText));
                order.setOrderState(OrderState.ENTER_REASON);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Будь ласка назвіть причину, чому Ви не можете самостійно забезпечити себе їжею та/або ліками.").build();
            }
            case ENTER_REASON -> {
                if (!DataValidUtil.validName(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Введений текст не повинен перевищувати 1000 символів.").build();
                }
                order.setReason(messageText);
                order.setOrderState(OrderState.ENTER_PHONE);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Введіть номер телефона за прикладом: +380507772200 або 0507772200.").build();
            }
            case ENTER_PHONE -> {
                if (!DataValidUtil.validPhone(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Номер телефона введено неправильно! Напишіть коректно за прикладом: +380507772200 або 0507772200.").build();
                }
                order.setPhoneNumber(messageText);
                order.setOrderState(OrderState.ENTER_ADDRESS);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Введіть точний адрес, на який Вам привезти допомогу.").build();
            }
            case ENTER_ADDRESS -> {
                if (!DataValidUtil.validAddress(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Адрес введено неправильно! Доступна кількість символів від 2 до 300.").build();
                }
                order.setAddress(messageText);
                order.setOrderState(OrderState.ENTER_FOOD);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Введіть, яка їжа Вам потрібна.").build();
            }
            case ENTER_FOOD -> {
                if (!DataValidUtil.validText(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Введений текст не повинен перевищувати 1000 символів.").build();
                }
                order.setFood(messageText);
                order.setOrderState(OrderState.ENTER_MEDICINES);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Введіть, які ліки Вам потрібні.").build();
            }
            case ENTER_MEDICINES -> {
                if (!DataValidUtil.validText(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Введений текст не повинен перевищувати 1000 символів.").build();
                }
                order.setMedicines(messageText);
                order.setOrderState(OrderState.ENTER_OTHER);
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Введіть примітки або додаткову інформацію.").build();
            }
            case ENTER_OTHER -> {
                if (!DataValidUtil.validText(messageText)) {
                    return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Помилка! Введений текст не повинен перевищувати 1000 символів.").build();
                }
                order.setOtherInfo(messageText);
                order.setOrderState(OrderState.NONE);
                botUser.setState(State.START);
                orderService.create(order);
                botUserService.update(botUser);
                orderCache.removeByChatId(message.getChatId());
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Ваш запит принято! Скоро вам зателефонує наш волонтер.").build();
            }
            default -> throw new UnsupportedOperationException("Unknown status");
        }
    }
}
