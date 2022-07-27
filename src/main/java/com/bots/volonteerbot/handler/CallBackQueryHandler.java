package com.bots.volonteerbot.handler;

import com.bots.volonteerbot.cache.OrderCache;
import com.bots.volonteerbot.persistence.datatable.DataTableRequest;
import com.bots.volonteerbot.persistence.entity.Order;
import com.bots.volonteerbot.persistence.repository.OrderRepository;
import com.bots.volonteerbot.service.OrderService;
import com.bots.volonteerbot.util.ToStringUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Service
public class CallBackQueryHandler implements Handler<CallbackQuery> {

    private final OrderCache orderCache;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public CallBackQueryHandler(OrderCache orderCache, OrderRepository orderRepository, OrderService orderService) {
        this.orderCache = orderCache;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Override
    public SendMessage choose(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        if (callbackQuery.getData().equals("/done")) {
            final Order order = orderCache.findByChatId(chatId);
            orderCache.removeByChatId(chatId);
            orderRepository.deleteById(order.getId());
            return SendMessage.builder().chatId(String.valueOf(chatId)).text("Запит виконано! Вибери наступний").build();
        } else {
            int page = Integer.parseInt(callbackQuery.getData());
            DataTableRequest dataTableRequest = DataTableRequest.fromPage(orderRepository.count(), page);
            List<Order> orders = orderService.findAll(dataTableRequest);
            String messageText = ToStringUtil.getAllOrdersToString(orders, dataTableRequest);
            return SendMessage.builder()
                    .parseMode("HTML")
                    .chatId(String.valueOf(chatId))
                    .text(messageText)
                    .replyMarkup(ToStringUtil.getKeyboard(dataTableRequest)).build();

        }
    }
}
