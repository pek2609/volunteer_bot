package com.bots.volonteerbot.cache.impl;

import com.bots.volonteerbot.cache.BotUserCache;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.repository.BotUserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BotUserCacheImpl implements BotUserCache {

    private final Map<Long, BotUser> botUserMap;

    public BotUserCacheImpl(BotUserRepository orderRepository) {
        this.botUserMap = new HashMap<>();
    }

    @Override
    public BotUser findByChatId(Long id) {
        return botUserMap.get(id);
    }

    @Override
    public void removeByChatId(Long id) {
        botUserMap.remove(id);
    }

    @Override
    public void add(BotUser botUser, Long chatId) {
        botUserMap.put(chatId, botUser);
    }
}
