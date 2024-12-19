package com.example.Checkout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductOrderDto(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be greater than 0")
        Integer quantity
) {

}
