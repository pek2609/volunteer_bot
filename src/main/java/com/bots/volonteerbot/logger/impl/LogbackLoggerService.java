package com.bots.volonteerbot.logger.impl;

import com.bots.volonteerbot.logger.LoggerLevel;
import com.bots.volonteerbot.logger.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class LogbackLoggerService implements LoggerService {

    private static final Logger LOGGER_INFO = LoggerFactory.getLogger(LoggerLevel.INFO.getLevel());
    private static final Logger LOGGER_WARN = LoggerFactory.getLogger(LoggerLevel.WARN.getLevel());
    private static final Logger LOGGER_ERROR = LoggerFactory.getLogger(LoggerLevel.ERROR.getLevel());

    @Override
    public void commit(LoggerLevel level, String message) {
        switch (level) {
            case INFO -> LOGGER_INFO.info(message);
            case WARN -> LOGGER_WARN.warn(message);
            case ERROR -> LOGGER_ERROR.error(message);
        }
    }
}
