package com.bots.volonteerbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Handler<T> {

    SendMessage choose(T t);
}
