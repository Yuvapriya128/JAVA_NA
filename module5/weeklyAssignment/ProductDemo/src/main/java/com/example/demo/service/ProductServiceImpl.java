package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepo;
import com.example.demo.specification.ProductSpecificationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
/*
* or create this inside class
* public static final Logger log=LoggerFacgory.getLogger(ProductServiceImpl.class);
* */
public class ProductServiceImpl implements ProductService {

	private final ProductRepo productRepo;
	private final ProductSpecificationBuilder specificationBuilder;

	public ProductServiceImpl(ProductRepo productRepo, ProductSpecificationBuilder specificationBuilder) {
		this.productRepo = productRepo;
		this.specificationBuilder = specificationBuilder;
	}


	@Override
	public Product saveProduct(Product product) {

		log.info("LOG INFO: product: {}",product);
		log.warn("LOG WARN: product arrived");
		log.debug("LOG DEBUG: product details: {}", product);
		log.trace("LOG TRACE: product details: {}", product);
		if(product.getCost()<0) {
			log.error("LOG ERROR: product details: {}", product);
			throw new RuntimeException("Product cost cannot be negative");
		}

		log.info("product will be saved");
		return productRepo.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		log.info("all products are fetched");
		return productRepo.findAll();
	}

	@Override
	public Optional<Product> getProductById(Integer id) {
		log.info("product is fetched by id");
		log.info("product with id={} is fetched", id);
		return productRepo.findById(id);
	}

	@Override
	public Product updateProduct(Product product) {
		log.info("product is updated");
		return productRepo.save(product);
	}

	@Override
	public void deleteProductById(Integer id) {
		log.info("product with id={} is deleted", id);
		productRepo.deleteById(id);
	}

	@Override
	public List<Product> searchProducts(String brand, String name, String category, String sort) {
		Specification<Product> specification = buildSpecification(brand, name, category);
		Sort sortBy = buildSort(sort);

		if (!hasFilters(brand, name, category) && sortBy.isUnsorted()) {
			log.info("search requested without filters and sort");
			return productRepo.findAll();
		}

		return productRepo.findAll(specification, sortBy);
	}

	@Override
	public List<Product> getAllProductsSortedByCost(boolean ascending) {
		Sort sort = ascending ? Sort.by("cost").ascending() : Sort.by("cost").descending();
		log.info("products fetched with cost sort order: {}", ascending ? "asc" : "desc");
		return productRepo.findAll(sort);
	}

	@Override
	public List<Product> filterProducts(String brand, String name, String category) {
		log.info("filtering products by brand={}, name={}, category={}", brand, name, category);
		return searchProducts(brand, name, category, null);
	}

	private Specification<Product> buildSpecification(String brand, String name, String category) {
		return specificationBuilder.buildSpecification(brand, name, category);
	}

	private Sort buildSort(String sort) {
		if (sort == null || sort.isBlank()) {
			return Sort.unsorted();
		}
		if ("desc".equalsIgnoreCase(sort)) {
			return Sort.by("cost").descending();
		}
		if ("asc".equalsIgnoreCase(sort)) {
			return Sort.by("cost").ascending();
		}
		return Sort.unsorted();
	}

	private boolean hasFilters(String brand, String name, String category) {
		return hasText(brand) || hasText(name) || hasText(category);
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
