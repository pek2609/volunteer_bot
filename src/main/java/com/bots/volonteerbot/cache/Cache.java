package com.bots.volonteerbot.cache;

import com.bots.volonteerbot.persistence.entity.BaseEntity;

public interface Cache<T extends BaseEntity> {

    T findByChatId(Long id);

    void removeByChatId(Long id);

    void add(T t, Long chatId);
}
