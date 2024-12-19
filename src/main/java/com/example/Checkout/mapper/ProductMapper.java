package com.example.Checkout.mapper;


import com.example.Checkout.dto.ProductDto;
import com.example.Checkout.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "product.id", ignore = true)
    ProductDto entityToProductDto(Product product);
    Product productDtoToEntity(ProductDto productDto);
}

