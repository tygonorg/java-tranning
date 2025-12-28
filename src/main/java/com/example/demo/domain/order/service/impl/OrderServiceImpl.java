package com.example.demo.domain.order.service.impl;

import com.example.demo.domain.order.dto.OrderDTO;
import com.example.demo.domain.order.dto.OrderItemDTO;
import com.example.demo.domain.order.entity.Order;
import com.example.demo.domain.order.entity.OrderItem;
import com.example.demo.domain.order.repository.OrderRepository;
import com.example.demo.domain.order.service.OrderService;
import com.example.demo.domain.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (orderDTO.getItems() != null) {
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                if (itemDTO.getQuantity() <= 0) {
                    throw new com.example.demo.domain.shared.exception.BusinessException(
                            "Quantity must be greater than 0");
                }
                OrderItem item = new OrderItem();
                item.setBookId(itemDTO.getBookId());
                item.setUnitPrice(itemDTO.getUnitPrice());
                item.setQuantity(itemDTO.getQuantity());
                item.setOrder(order);
                items.add(item);

                BigDecimal itemTotal = itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}
