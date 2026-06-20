package org.example.jpademo.controller;

import org.example.jpademo.model.Product;
import org.example.jpademo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
* Write application properties: for connecting db
* Write restcontroller to do this in web
* Write ResponseEntity to give status codes
*
*
*
* */

@RestController
@RequestMapping("/api/productsjpa")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("")
    public ResponseEntity<List<Product>> getAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable int id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<Product> save(@RequestBody Product product){
        return ResponseEntity.status(201).body(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateById(@PathVariable int id,@RequestBody Product product){
        return ResponseEntity.ok(productService.updateById(id,product));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id){
         productService.deleteById(id);
         return ResponseEntity.noContent().build();
    }
}
