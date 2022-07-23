package com.bots.volonteerbot.cache.impl;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.persistence.entity.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderCacheImpl implements OrderCache {

    private final Map<Long, Order> orderMap;

    public OrderCacheImpl() {
        this.orderMap = new HashMap<>();
    }

    @Override
    public Order findByChatId(Long id) {
        return orderMap.get(id);
    }

    @Override
    public void removeByChatId(Long id) {
        orderMap.remove(id);
    }

    @Override
    public void add(Order order, Long chatId) {
        orderMap.put(chatId, order);
    }
}
