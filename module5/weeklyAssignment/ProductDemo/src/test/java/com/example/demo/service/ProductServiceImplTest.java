package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
//if @SpringBootTest -> @MockBean
//@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @MockBean
    private ProductRepo productRepo;

//    @InjectMocks
    @Autowired
    private ProductServiceImpl productService;

    @Test
    @DisplayName("save products")
    void saveCheck(){
        Product product=new Product(1,"fridge","lg","electronics",2000);

        when(productRepo.save(product)).thenReturn(product);
        Product savedProduct=productService.saveProduct(product);
        Assertions.assertEquals(savedProduct.getName(),product.getName());
        Assertions.assertEquals(savedProduct.getBrand(),product.getBrand());
        Assertions.assertEquals(savedProduct.getCategory(),product.getCategory());
        Assertions.assertEquals(savedProduct.getCost(),product.getCost());
        Assertions.assertNotNull(savedProduct.getId());
        Mockito.verify(productRepo).save(product);
    }


}
