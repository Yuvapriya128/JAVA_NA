package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.exceptions.ProductNotFound;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepo productRepo;

    @Override
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepo.findById(id).orElseThrow(()->new ProductNotFound("Product not found"));
    }

    @Override
    public Product updateProduct(Integer id, Product product) {
        Product tempProduct=productRepo.findById(id).orElseThrow(()->new ProductNotFound("Product not found : for updating"));
        tempProduct.setName(product.getName());
        tempProduct.setCost(product.getCost());
        tempProduct.setBrand(product.getBrand());
        tempProduct.setCategory(product.getCategory());

//        Usually we don't update relationships while updating a product.
//        tempProduct.setOrderItems(product.getOrderItems());

        return productRepo.save(tempProduct);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepo.deleteById(id);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepo.findProductByCategory(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepo.findProductByBrand(brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepo.findProductByName(name);
    }
}
