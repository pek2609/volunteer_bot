package com.bots.volonteerbot.bot;

import com.bots.volonteerbot.processor.Processor;
import com.bots.volonteerbot.util.SendMessageSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class Bot extends TelegramLongPollingBot {

    @Value("${telegram.bot.name}")
    private String userName;

    @Value("${telegram.bot.token}")
    private String token;

    private final Processor processor;

    public Bot(Processor processor) {
        this.processor = processor;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sm = processor.process(update);
        if (sm != null) {
            try {
                if (sm.getText().length() > 4096) {
                    for (SendMessage sendMessage : SendMessageSplitter.splitMessage(sm)) {
                        execute(sendMessage);
                    }
                } else {
                    execute(sm);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
