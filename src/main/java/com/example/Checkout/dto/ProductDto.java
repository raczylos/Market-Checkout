package com.example.Checkout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductDto(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Price cannot be null")
        @Min(value = 1, message = "Normal price must be greater than 0")
        Integer normalPrice,
        @Min(value = 1, message = "Required quantity must be greater than 0")
        Integer requiredQuantity,
        @Min(value = 1, message = "Special price must be greater than 0")
        Integer specialPrice
) {
}
