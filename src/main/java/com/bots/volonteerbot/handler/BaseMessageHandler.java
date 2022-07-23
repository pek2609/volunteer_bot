package com.bots.volonteerbot.handler;

import com.bots.volonteerbot.exception.EntityNotFoundException;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.type.RoleType;
import com.bots.volonteerbot.persistence.type.State;
import com.bots.volonteerbot.service.BotUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class BaseMessageHandler implements Handler<Message> {

    private final AdminMessageHandler adminMessageHandler;
    private final UserMessageHandler userMessageHandler;
    private final BotUserService botUserService;

    public BaseMessageHandler(AdminMessageHandler adminMessageHandler, UserMessageHandler userMessageHandler, BotUserService botUserService) {
        this.adminMessageHandler = adminMessageHandler;
        this.userMessageHandler = userMessageHandler;
        this.botUserService = botUserService;
    }

    @Override
    public SendMessage choose(Message message) {
        if (StringUtils.isBlank(message.getText())) {
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Не можу проаналізувати повідомлення, я розумію лиш текстові меседжи").build();
        }
        try {
            BotUser botUser = botUserService.findByChatId(message.getChatId());
            if (botUser.getRole() == RoleType.ROLE_ADMIN) {
                return adminMessageHandler.choose(message);
            } else {
                return userMessageHandler.choose(message);
            }
        } catch (EntityNotFoundException e) {
            BotUser botUser = new BotUser();
            botUser.setChatId(message.getChatId());
            botUser.setUserName(message.getChat().getUserName());
            botUser.setRole(RoleType.ROLE_USER);
            botUser.setState(State.NONE);
            botUserService.create(botUser);
            return userMessageHandler.choose(message);
        }
    }
}
