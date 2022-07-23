package com.bots.volonteerbot.logger;

public interface LoggerService {

    void commit(LoggerLevel level, String message);
}
