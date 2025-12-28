package com.example.demo.domain.order.service;

import com.example.demo.domain.order.dto.OrderDTO;
import com.example.demo.domain.order.entity.Order;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);

    Order getOrderById(Long id);
}
