package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.DTO.ProductRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductUpdateDTO;
import org.example.springdatajpademo.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDTO> save(
            @Valid @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.status(201)
                .body(productService.saveProduct(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateDTO dto) {

        return ResponseEntity.ok(
                productService.updateProduct(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteById(
            @PathVariable Integer id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(category)
        );
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponseDTO>> findByBrand(
            @PathVariable String brand) {

        return ResponseEntity.ok(
                productService.getProductsByBrand(brand)
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ProductResponseDTO>> findByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                productService.getProductsByName(name)
        );
    }
}