package com.example.Checkout.controller;

import com.example.Checkout.dto.ProductDto;

import com.example.Checkout.dto.ProductOrderDto;
import com.example.Checkout.dto.PurchasedProductDto;
import com.example.Checkout.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ProductDto addProduct(@Valid @RequestBody ProductDto product) {
        return productService.addProduct(product);
    }

    @GetMapping()
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{name}")
    public ProductDto getProductByName(@PathVariable String name) {
        return productService.getProductByName(name);
    }

    @PostMapping("/calculate")
    public List<PurchasedProductDto> calculate(@Valid @RequestBody List<@Valid ProductOrderDto> productsOrder) {
        return productService.calculateProducts(productsOrder);
    }
}
