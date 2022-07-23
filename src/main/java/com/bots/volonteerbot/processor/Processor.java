package com.bots.volonteerbot.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {

    SendMessage executeMessage(Message message);

    SendMessage executeCallBackQuery(CallbackQuery callbackQuery);

    default SendMessage process(Update update) {
        if (update.hasMessage()) {
            return executeMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            return executeCallBackQuery(update.getCallbackQuery());
        }
        return null;
    }
}
