package com.example.Checkout.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "normal_price", nullable = false)
    private Integer normalPrice;

    @Column(name = "required_quantity")
    private Integer requiredQuantity;

    @Column(name = "special_price")
    private Integer specialPrice;

    public Product(String name, Integer normalPrice, Integer requiredQuantity, Integer specialPrice) {
        this.name = name;
        this.normalPrice = normalPrice;
        this.requiredQuantity = requiredQuantity;
        this.specialPrice = specialPrice;
    }
}
