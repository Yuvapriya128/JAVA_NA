package com.example.demo.specification;

import com.example.demo.model.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ProductSpecificationBuilder {

    public Specification<Product> buildSpecification(String brand, String name, String category) {
        return Specification.where(applyBrandFilter(brand))
                .and(applyNameFilter(name))
                .and(applyCategoryFilter(category));
    }

    public Specification<Product> applyNameFilter(String name) {
        if (!hasText(name)) {
            return null;
        }
        String pattern = buildLikePattern(name);
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public Specification<Product> applyCategoryFilter(String category) {
        if (!hasText(category)) {
            return null;
        }
        String pattern = buildLikePattern(category);
        return (root, query, cb) -> cb.like(cb.lower(root.get("category")), pattern);
    }

    public Specification<Product> applyBrandFilter(String brand) {
        if (!hasText(brand)) {
            return null;
        }
        String pattern = buildLikePattern(brand);
        return (root, query, cb) -> cb.like(cb.lower(root.get("brand")), pattern);
    }

    private String buildLikePattern(String value) {
        return "%" + value.toLowerCase(Locale.ROOT).trim() + "%";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

