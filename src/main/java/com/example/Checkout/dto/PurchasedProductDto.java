package com.example.Checkout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchasedProductDto(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be greater than 0")
        Integer quantity,
        @NotNull(message = "Cost cannot be null")
        @Min(value = 0, message = "Cost must be greater or equal 0")
        Integer cost
) {

}
