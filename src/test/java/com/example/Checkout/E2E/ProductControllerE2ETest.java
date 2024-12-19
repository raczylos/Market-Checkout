package com.example.Checkout.E2E;

import com.example.Checkout.dto.ProductDto;
import com.example.Checkout.dto.ProductOrderDto;
import com.example.Checkout.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    protected void afterEach() {
        productRepository.deleteAll();
    }

    @Test
    void shouldAddNewProduct() {
        ProductDto product = new ProductDto(
                "TestProduct",
                150,
                2,
                100
        );

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(product.name()))
                .body("normalPrice", equalTo(product.normalPrice()))
                .body("requiredQuantity", equalTo(product.requiredQuantity()))
                .body("specialPrice", equalTo(product.specialPrice()));
    }

    @Test
    void shouldNotAllowAddingDuplicateProduct() {
        ProductDto product = new ProductDto(
                "ExistingProduct",
                150,
                2,
                100
        );

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("Product already exists"));
    }

    @Test
    void shouldFetchAllProducts() {

        ProductDto product1 = new ProductDto(
                "TestProduct1",
                150,
                2,
                100
        );

        ProductDto product2 = new ProductDto(
                "TestProduct2",
                150,
                2,
                100
        );

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product1)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product2)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(2))
                .body("[0].name", equalTo(product1.name()))
                .body("[1].name", equalTo(product2.name()));

    }

    @Test
    void shouldFetchProductByName() {
        ProductDto product = new ProductDto(
                "TestProduct1",
                150,
                2,
                100
        );

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .log().ifValidationFails()
                .get("/api/products/{name}", product.name())
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldCalculateProducts() {
        ProductDto product1 = new ProductDto(
                "TestProduct1",
                150,
                2,
                100
        );

        ProductDto product2 = new ProductDto(
                "TestProduct2",
                100,
                3,
                50
        );

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product1)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(product2)
                .when()
                .post("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value());

        ProductOrderDto productToOrder1 = new ProductOrderDto(product1.name(), 5);
        ProductOrderDto productToOrder2 = new ProductOrderDto(product2.name(), 4);
        List<ProductOrderDto> productsToOrder = Arrays.asList(productToOrder1, productToOrder2);

        RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON)
                .body(productsToOrder)
                .when()
                .log().ifValidationFails()
                .post("/api/products/calculate")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(2))
                .body("[0].name", equalTo(productToOrder1.name()))
                .body("[0].quantity", equalTo(productToOrder1.quantity()))
                .body("[0].cost", equalTo(550))
                .body("[1].name", equalTo(productToOrder2.name()))
                .body("[1].quantity", equalTo(productToOrder2.quantity()))
                .body("[1].cost", equalTo(250));
    }
}
