package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.ProductRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductUpdateDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO saveProduct(ProductRequestDTO productDTO);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(Integer id);

    ProductResponseDTO updateProduct(Integer id,
                                     ProductUpdateDTO productDTO);

    void deleteProduct(Integer id);

    List<ProductResponseDTO> getProductsByCategory(String category);

    List<ProductResponseDTO> getProductsByBrand(String brand);

    List<ProductResponseDTO> getProductsByName(String name);
}