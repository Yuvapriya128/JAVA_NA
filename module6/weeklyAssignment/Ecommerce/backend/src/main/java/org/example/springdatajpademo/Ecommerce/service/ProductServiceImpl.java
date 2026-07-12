package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.ProductRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.ProductNotFound;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO saveProduct(ProductRequestDTO dto) {

        Product product = mapToEntity(dto);

        Product savedProduct = productRepo.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ProductResponseDTO> getAllProducts() {

        return productRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ProductResponseDTO getProductById(Integer id) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ProductNotFound("Product not found"));

        return mapToResponse(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO updateProduct(Integer id,
                                            ProductUpdateDTO dto) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ProductNotFound("Product not found"));

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setCategory(dto.getCategory());
        product.setCost(dto.getCost());

        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        if (dto.getMinimumStock() != null) {
            product.setMinimumStock(dto.getMinimumStock());
        }
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }

        Product updatedProduct = productRepo.save(product);

        return mapToResponse(updatedProduct);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Integer id) {
        productRepo.deleteById(id);
    }

    // Validate stock for ordering
    public boolean validateStock(Integer productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        return product.getStock() >= quantity;
    }

    // Reduce stock after order
    public void reduceStock(Integer productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock available");
        }
        product.setStock(product.getStock() - quantity);
        productRepo.save(product);
    }

    // Restore stock on order cancellation
    public void restoreStock(Integer productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found"));

        product.setStock(product.getStock() + quantity);
        productRepo.save(product);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ProductResponseDTO> getProductsByCategory(String category) {

        return productRepo.findProductByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ProductResponseDTO> getProductsByBrand(String brand) {

        return productRepo.findProductByBrand(brand)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ProductResponseDTO> getProductsByName(String name) {

        return productRepo.findProductByName(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ==========================
    // Mapping Methods
    // ==========================

    private Product mapToEntity(ProductRequestDTO dto) {

        Product product = new Product();

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setCategory(dto.getCategory());
        product.setCost(dto.getCost());
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setMinimumStock(dto.getMinimumStock() != null ? dto.getMinimumStock() : 10);
        product.setActive(true);

        return product;
    }

    private ProductResponseDTO mapToResponse(Product product) {

        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setCategory(product.getCategory());
        dto.setCost(product.getCost());
        dto.setDescription(product.getDescription());
        dto.setStock(product.getStock());
        dto.setMinimumStock(product.getMinimumStock());
        dto.setActive(product.getActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }
}