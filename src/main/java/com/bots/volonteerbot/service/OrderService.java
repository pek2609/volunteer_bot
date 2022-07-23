package com.bots.volonteerbot.service;

import com.bots.volonteerbot.persistence.datatable.DataTableRequest;
import com.bots.volonteerbot.persistence.entity.Order;

import java.util.List;

public interface OrderService extends BaseService<Order> {

    List<Order> findAll(DataTableRequest dataTableRequest);

}

