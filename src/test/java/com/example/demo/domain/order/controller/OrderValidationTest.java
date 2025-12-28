package com.example.demo.domain.order.controller;

import com.example.demo.domain.order.dto.OrderDTO;
import com.example.demo.domain.order.dto.OrderItemDTO;
import com.example.demo.domain.order.service.OrderService;
import com.example.demo.domain.shared.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@org.springframework.context.annotation.Import(com.example.demo.infrastructure.exception.GlobalExceptionHandler.class)
public class OrderValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateOrder_InvalidQuantity() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setBookId(1L);
        itemDTO.setUnitPrice(BigDecimal.valueOf(100));
        itemDTO.setQuantity(0); // Invalid quantity
        orderDTO.setItems(List.of(itemDTO));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    public void testCreateOrder_BusinessException() throws Exception {
        // Checking if BusinessException is handled correctly
        given(orderService.createOrder(any(OrderDTO.class))).willThrow(new BusinessException("Custom Error"));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setBookId(1L);
        itemDTO.setUnitPrice(BigDecimal.valueOf(100));
        itemDTO.setQuantity(2); // Valid
        orderDTO.setItems(List.of(itemDTO));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Custom Error"));
    }
}
