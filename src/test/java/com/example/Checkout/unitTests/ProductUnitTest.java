package com.example.Checkout.unitTests;

import com.example.Checkout.dto.ProductDto;
import com.example.Checkout.dto.ProductOrderDto;
import com.example.Checkout.dto.PurchasedProductDto;
import com.example.Checkout.mapper.ProductMapper;
import com.example.Checkout.model.Product;
import com.example.Checkout.repository.ProductRepository;
import com.example.Checkout.service.ProductService;
import com.example.Checkout.exception.ProductAlreadyExistsException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductUnitTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct_ShouldAddProductWhenNotExists() {

        ProductDto productDto = new ProductDto(
                "TestProduct1",
                150,
                2,
                100
        );
        Product productEntity = new Product(
                "TestProduct1",
                150,
                2,
                100
        );

        when(productRepository.findByName(productDto.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(productDto)).thenReturn(productEntity);

        ProductDto result = productService.addProduct(productDto);

        verify(productRepository).save(productEntity);
        assertEquals(productDto.name(), result.name());
        assertEquals(productDto.normalPrice(), result.normalPrice());
        assertEquals(productDto.requiredQuantity(), result.requiredQuantity());
        assertEquals(productDto.specialPrice(), result.specialPrice());
    }

    @Test
    void addProduct_ShouldThrowExceptionWhenProductAlreadyExists() {

        ProductDto productDto = new ProductDto(
                "TestProduct1",
                150,
                2,
                100
        );
        Product existingProduct = new Product(
                "TestProduct1",
                150,
                2,
                100
        );

        when(productRepository.findByName(productDto.name())).thenReturn(Optional.of(existingProduct));

        ProductAlreadyExistsException exception = assertThrows(ProductAlreadyExistsException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Product already exists", exception.getMessage());
    }

    @Test
    void getAllProducts() {

        ProductDto productDto1 = new ProductDto("TestProduct1", 150, 2, 100);
        ProductDto productDto2 = new ProductDto("TestProduct2", 150, 2, 100);

        Product product1 = new Product("TestProduct1", 150, 2, 100);
        Product product2 = new Product("TestProduct2", 150, 2, 100);

        when(productRepository.findByName(productDto1.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(productDto1)).thenReturn(product1);
        when(productRepository.findByName(productDto2.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(productDto2)).thenReturn(product2);

        productService.addProduct(productDto1);
        productService.addProduct(productDto2);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(productMapper.entityToProductDto(product1)).thenReturn(productDto1);
        when(productMapper.entityToProductDto(product2)).thenReturn(productDto2);

        List<ProductDto> fetchedProducts = productService.getAllProducts();

        assertNotNull(fetchedProducts);
        assertEquals(2, fetchedProducts.size());
        assertEquals("TestProduct1", fetchedProducts.get(0).name());
        assertEquals("TestProduct2", fetchedProducts.get(1).name());

    }

    @Test
    void getProductByName() {

        ProductDto productDto = new ProductDto("TestProduct1", 150, 2, 100);
        Product productEntity = new Product("TestProduct1", 150, 2, 100);

        when(productRepository.findByName(productDto.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(productDto)).thenReturn(productEntity);

        productService.addProduct(productDto);

        when(productRepository.findByName(productDto.name())).thenReturn(Optional.of(productEntity));
        when(productMapper.entityToProductDto(productEntity)).thenReturn(productDto);

        ProductDto result = productService.getProductByName(productDto.name());

        assertNotNull(result);
        assertEquals(productDto.name(), result.name());
        assertEquals(productDto.normalPrice(), result.normalPrice());
        assertEquals(productDto.requiredQuantity(), result.requiredQuantity());
        assertEquals(productDto.specialPrice(), result.specialPrice());

    }

    @Test
    void calculateProducts() {

        ProductDto product1 = new ProductDto("TestProduct1", 150, 2, 100);
        ProductDto product2 = new ProductDto("TestProduct2", 100, 3, 50);

        Product productEntity1 = new Product("TestProduct1", 150, 2, 100);
        Product productEntity2 = new Product("TestProduct2", 100, 3, 50);

        when(productRepository.findByName(product1.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(product1)).thenReturn(productEntity1);
        when(productRepository.findByName(product2.name())).thenReturn(Optional.empty());
        when(productMapper.productDtoToEntity(product2)).thenReturn(productEntity2);

        productService.addProduct(product1);
        productService.addProduct(product2);

        ProductOrderDto productToOrder1 = new ProductOrderDto(product1.name(), 5);
        ProductOrderDto productToOrder2 = new ProductOrderDto(product2.name(), 4);
        List<ProductOrderDto> productsToOrder = Arrays.asList(productToOrder1, productToOrder2);

        when(productRepository.findByName(product1.name())).thenReturn(Optional.of(productEntity1));
        when(productRepository.findByName(product2.name())).thenReturn(Optional.of(productEntity2));
        List<PurchasedProductDto> calculatedProducts = productService.calculateProducts(productsToOrder);

        assertEquals(2, calculatedProducts.size());

        PurchasedProductDto calculatedProduct1 = calculatedProducts.get(0);
        assertEquals(productToOrder1.name(), calculatedProduct1.name());
        assertEquals(productToOrder1.quantity(), calculatedProduct1.quantity());
        assertEquals(550, calculatedProduct1.cost());

        PurchasedProductDto calculatedProduct2 = calculatedProducts.get(1);
        assertEquals(productToOrder2.name(), calculatedProduct2.name());
        assertEquals(productToOrder2.quantity(), calculatedProduct2.quantity());
        assertEquals(250, calculatedProduct2.cost());

    }
}
