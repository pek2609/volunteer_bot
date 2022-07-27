package com.bots.volonteerbot.service.impl;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.exception.EntityNotFoundException;
import com.bots.volonteerbot.exception.OrderNotFoundException;
import com.bots.volonteerbot.persistence.datatable.DataTableRequest;
import com.bots.volonteerbot.persistence.entity.Order;
import com.bots.volonteerbot.persistence.repository.OrderRepository;
import com.bots.volonteerbot.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderCache orderCache;

    public OrderServiceImpl(OrderRepository orderRepository, OrderCache orderCache) {
        this.orderRepository = orderRepository;
        this.orderCache = orderCache;
    }

    @Transactional
    @Override
    public void create(Order entity) {
        orderRepository.save(entity);
    }

    @Transactional
    @Override
    public void update(Order entity) {
        orderRepository.save(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public boolean existByChatId(Long chatId) {
        return orderRepository.existsByBotUserChatId(chatId);
    }

    @Transactional(readOnly = true)
    @Override
    public Order findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Запросу не існує");
        }
        return order.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Order findByChatId(Long chatId) {
        Order order = orderCache.findByChatId(chatId);
        if (order == null) {
            final List<Order> orderRep = orderRepository.findByBotUserChatId(chatId);
            if (orderRep.size() == 0) {
                throw new OrderNotFoundException("Запросу не існує");
            }
            order = orderRep.get(0);
            orderCache.add(order, chatId);
        }
        return order;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Order> findAll(DataTableRequest dataTableRequest) {
        return orderRepository.findAll(dataTableRequest.pageRequest()).getContent();
    }
}
