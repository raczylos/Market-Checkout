package com.example.Checkout.integrationTests;

import com.example.Checkout.dto.ProductDto;
import com.example.Checkout.dto.ProductOrderDto;
import com.example.Checkout.dto.PurchasedProductDto;
import com.example.Checkout.exception.ProductAlreadyExistsException;
import com.example.Checkout.model.Product;
import com.example.Checkout.repository.ProductRepository;
import com.example.Checkout.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private ProductDto productDto1;
    private ProductDto productDto2;

    @BeforeEach
    void setUp() {
        productDto1 = new ProductDto("TestProduct1", 150, 2, 100);
        productDto2 = new ProductDto("TestProduct2", 100, 3, 50);
    }

    @AfterEach
    void afterEach() {
        productRepository.deleteAll();
    }

    @Test
    void shouldAddProductWhenNotExists() {
        productService.addProduct(productDto1);

        Product productFromDb = productRepository.findByName(productDto1.name()).orElseThrow();
        assertNotNull(productFromDb);
        assertEquals(productDto1.name(), productFromDb.getName());
        assertEquals(productDto1.normalPrice(), productFromDb.getNormalPrice());
        assertEquals(productDto1.requiredQuantity(), productFromDb.getRequiredQuantity());
        assertEquals(productDto1.specialPrice(), productFromDb.getSpecialPrice());
    }

    @Test
    void shouldNotAllowAddingDuplicateProduct() {
        productService.addProduct(productDto1);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.addProduct(productDto1));
    }

    @Test
    void shouldFetchAllProducts() {
        productService.addProduct(productDto1);
        productService.addProduct(productDto2);
        List<Product> productsFromDb = productRepository.findAll();

        assertNotNull(productsFromDb);

        assertEquals(productDto1.name(), productsFromDb.get(0).getName());
        assertEquals(productDto1.normalPrice(), productsFromDb.get(0).getNormalPrice());
        assertEquals(productDto1.requiredQuantity(), productsFromDb.get(0).getRequiredQuantity());
        assertEquals(productDto1.specialPrice(), productsFromDb.get(0).getSpecialPrice());

        assertEquals(productDto2.name(), productsFromDb.get(1).getName());
        assertEquals(productDto2.normalPrice(), productsFromDb.get(1).getNormalPrice());
        assertEquals(productDto2.requiredQuantity(), productsFromDb.get(1).getRequiredQuantity());
        assertEquals(productDto2.specialPrice(), productsFromDb.get(1).getSpecialPrice());
    }

    @Test
    void shouldFetchProductByName() {
        productService.addProduct(productDto1);

        ProductDto fetchedProduct = productService.getProductByName(productDto1.name());

        assertNotNull(fetchedProduct);
        assertEquals(productDto1.name(), fetchedProduct.name());
        assertEquals(productDto1.normalPrice(), fetchedProduct.normalPrice());
        assertEquals(productDto1.requiredQuantity(), fetchedProduct.requiredQuantity());
        assertEquals(productDto1.specialPrice(), fetchedProduct.specialPrice());
    }

    @Test
    void calculateProducts() {
        productService.addProduct(productDto1);
        productService.addProduct(productDto2);

        ProductOrderDto productToOrder1 = new ProductOrderDto(productDto1.name(), 5);
        ProductOrderDto productToOrder2 = new ProductOrderDto(productDto2.name(), 4);

        List<ProductOrderDto> productsToOrder = Arrays.asList(productToOrder1, productToOrder2);

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
