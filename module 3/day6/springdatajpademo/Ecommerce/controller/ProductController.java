package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> findall(){
        return  ResponseEntity.ok(productService.getAllProducts());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable int id){
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product Product){
        return ResponseEntity.status(201).body(productService.saveProduct(Product));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable int id,@Valid @RequestBody Product Product){
        return ResponseEntity.ok(productService.updateProduct(id,Product));
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category){
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> findByBrand(@PathVariable String brand){
        return ResponseEntity.ok(productService.getProductsByBrand(brand));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Product>> findByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductsByName(name));
    }

}
