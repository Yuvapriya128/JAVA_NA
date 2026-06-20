package org.example.jpademo.service;


import org.example.jpademo.model.Product;

import java.util.List;

public interface ProductService {
    Product save(Product product);
    void deleteById(int id);
    Product findById(int id);
    List<Product> findAll();
    Product updateById(int id,Product product);
}
