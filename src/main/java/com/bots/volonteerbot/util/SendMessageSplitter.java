package com.bots.volonteerbot.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

public final class SendMessageSplitter {

    private SendMessageSplitter() {
    }

    public static List<SendMessage> splitMessage(SendMessage sendMessage) {
        return Streams.stream(Splitter.fixedLength(4096).split(sendMessage.getText()))
                .map(s -> SendMessage.builder()
                        .chatId(sendMessage.getChatId())
                        .parseMode("HTML")
                        .text(s).build())
                .collect(Collectors.toList());
    }
}

