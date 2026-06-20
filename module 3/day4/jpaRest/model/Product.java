package org.example.jpademo.model;

import jakarta.persistence.*;

@Entity
@Table(name="productjpa")
public class Product {
    @Id
    @GeneratedValue
    private int id;

    @Column(name="product_name")
    private String name;
    @Column(name="product_brand")
    private String brand;
    @Column(name="product_category")
    private String category;
    @Column(name="product_price")
    private double price;

    public Product() {
    }

    public Product(String name, String brand, String category, double price) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
    }

    public Product(int id, String name, String brand, String category, double price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
