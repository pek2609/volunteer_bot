package com.bots.volonteerbot.service.impl;

import com.bots.volonteerbot.cache.BotUserCache;
import com.bots.volonteerbot.exception.EntityNotFoundException;
import com.bots.volonteerbot.persistence.entity.BotUser;
import com.bots.volonteerbot.persistence.repository.BotUserRepository;
import com.bots.volonteerbot.service.BotUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BotUserServiceImpl implements BotUserService {

    private final BotUserRepository botUserRepository;
    private final BotUserCache botUserCache;

    public BotUserServiceImpl(BotUserRepository botUserRepository, BotUserCache botUserCache) {
        this.botUserRepository = botUserRepository;
        this.botUserCache = botUserCache;
    }

    @Transactional
    @Override
    public void create(BotUser entity) {
        botUserRepository.save(entity);
    }

    @Transactional
    @Override
    public void update(BotUser entity) {
        botUserRepository.save(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        botUserRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public BotUser findById(Long id) {
        Optional<BotUser> botUser = botUserRepository.findById(id);
        if (botUser.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return botUser.get();
    }

    @Transactional(readOnly = true)
    @Override
    public BotUser findByChatId(Long chatId) {
        BotUser botUser = botUserCache.findByChatId(chatId);
        if (botUser == null) {
            final Optional<BotUser> botUserRep = botUserRepository.findByChatId(chatId);
            if (botUserRep.isEmpty()) {
                throw new EntityNotFoundException("User doesn't exist");
            }
            botUser = botUserRep.get();
            botUserCache.add(botUser, chatId);
        }
        return botUser;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BotUser> findAll() {
        return botUserRepository.findAll();
    }
}
