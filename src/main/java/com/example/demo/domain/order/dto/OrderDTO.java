package com.example.demo.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import jakarta.validation.Valid;

@Data
public class OrderDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemDTO> items;
}
