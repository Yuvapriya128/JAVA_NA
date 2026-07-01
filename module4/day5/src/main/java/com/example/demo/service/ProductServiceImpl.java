package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
	private  ProductRepo productRepo;


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
}
