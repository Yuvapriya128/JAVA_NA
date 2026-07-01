package com.example.demo.service;

import com.example.demo.model.Product;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    Product saveProduct(Product product);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Integer id);

    Product updateProduct(Product product);

    void deleteProductById(Integer id);
}
