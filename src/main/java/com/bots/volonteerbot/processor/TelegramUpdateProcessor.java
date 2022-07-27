package com.bots.volonteerbot.processor;

import com.bots.volonteerbot.handler.CallBackQueryHandler;
import com.bots.volonteerbot.handler.BaseMessageHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class TelegramUpdateProcessor implements Processor{

    private final BaseMessageHandler messageHandler;
    private final CallBackQueryHandler callBackQueryHandler;

    public TelegramUpdateProcessor(BaseMessageHandler messageHandler, CallBackQueryHandler callBackQueryHandler) {
        this.messageHandler = messageHandler;
        this.callBackQueryHandler = callBackQueryHandler;
    }

    @Override
    public SendMessage executeMessage(Message message) {
        if (StringUtils.isBlank(message.getText())) {
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("Не можу проаналізувати повідомлення, я розумію лиш текстові меседжи").build();
        }
        try {
            return messageHandler.choose(message);
        } catch (RuntimeException exception) {
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text(exception.getMessage()).build();
        }
    }

    @Override
    public SendMessage executeCallBackQuery(CallbackQuery callbackQuery) {
        return callBackQueryHandler.choose(callbackQuery);
    }
}
