package org.example.jpademo.service;

import org.example.jpademo.model.Product;
import org.example.jpademo.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepo productRepo;

    @Override
    public Product save(Product product) {

        return productRepo.save(product);
    }

    @Override
    public void deleteById(int id) {
        productRepo.deleteById(id);

    }

    @Override
    public Product findById(int id) {

        return productRepo.findById(id).get();
    }

    @Override
    public List<Product> findAll() {

        return productRepo.findAll();
    }

    @Override
    public Product updateById(int id,Product product) {

        Product temp=productRepo.findById(id).get();

        temp.setBrand(product.getBrand());
        temp.setCategory(product.getCategory());
        temp.setName(product.getName());
        temp.setPrice(product.getPrice());

        return temp;
    }
}
