package org.example.springdatajpademo.Ecommerce.repository;

import org.example.springdatajpademo.Ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    public List<Product> findProductByBrand(String brand);
    public List<Product> findProductByCategory(String category);
    public List<Product> findProductByName(String name);
}
