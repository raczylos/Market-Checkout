package com.example.Checkout.service;

import com.example.Checkout.dto.ProductDto;
import com.example.Checkout.dto.ProductOrderDto;
import com.example.Checkout.dto.PurchasedProductDto;
import com.example.Checkout.mapper.ProductMapper;
import com.example.Checkout.model.Product;
import com.example.Checkout.repository.ProductRepository;
import com.example.Checkout.exception.ProductAlreadyExistsException;
import com.example.Checkout.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDto addProduct(ProductDto productDto) {
        productRepository.findByName(productDto.name()).ifPresent(data -> {
            throw new ProductAlreadyExistsException("Product already exists");
        });

        productRepository.save(productMapper.productDtoToEntity(productDto));

        return productDto;
    }

    public ProductDto getProductByName(String name) {
        var product = productRepository.findByName(name).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return productMapper.entityToProductDto(product);
    }

    public List<ProductDto> getAllProducts() {
        var products = productRepository.findAll();
        return products.stream()
                .map(productMapper::entityToProductDto)
                .toList();
    }

    public List<PurchasedProductDto> calculateProducts(List<ProductOrderDto> productsOrder) {

        List<PurchasedProductDto> purchasedProducts = new ArrayList<>();
        for(ProductOrderDto productOrder: productsOrder) {
            int price =  0;
            Product product = productRepository.findByName(productOrder.name()).orElseThrow(() ->
                    new ProductNotFoundException("Product not found"));

            if(productOrder.quantity() >= product.getRequiredQuantity()){
                var noDiscountedProductCount = productOrder.quantity() % product.getRequiredQuantity();
                if(noDiscountedProductCount > 0) {
                    price = price + (noDiscountedProductCount * product.getNormalPrice());
                }
                price = price + ((productOrder.quantity() - noDiscountedProductCount) * product.getSpecialPrice());
                purchasedProducts.add(new PurchasedProductDto(productOrder.name(), productOrder.quantity(), price));
            } else {
                price = price + (productOrder.quantity() * product.getNormalPrice());
                purchasedProducts.add(new PurchasedProductDto(productOrder.name(), productOrder.quantity(), price ));
            }
        }
        return purchasedProducts;
    }

}
