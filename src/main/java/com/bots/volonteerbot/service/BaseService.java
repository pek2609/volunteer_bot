package com.bots.volonteerbot.service;

import com.bots.volonteerbot.persistence.entity.BaseEntity;

import java.util.List;

public interface BaseService<E extends BaseEntity> {

    void create(E entity);

    void update(E entity);

    void delete(Long id);

    E findById(Long id);

    E findByChatId(Long chatId);

    List<E> findAll();

}
