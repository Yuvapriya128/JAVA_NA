package daoInJDBC.dao;

import daoInJDBC.entity.Product;

import java.sql.SQLException;
import java.util.Collection;

public interface ProductDao {

    // INSERT
    int save(Product product) throws SQLException;

    // BASIC SELECT
    Collection<Product> findAll() throws SQLException ;

    // WHERE
    Collection<Product> findByCategory(String category) throws SQLException ;

    // BETWEEN
    Collection<Product> findByPriceRange(
            double min,
            double max) throws SQLException ;

    // MULTIPLE CONDITIONS
    Collection<Product> findByCategoryAndBrand(
            String category,
            String brand) throws SQLException ;

    // ORDER BY
    Collection<Product> sortByPrice() throws SQLException ;

    // MULTIPLE ORDER BY
    Collection<Product> sortByPriceAndRating() throws SQLException ;

    // AGGREGATE
    double averagePrice() throws SQLException ;

    // GROUP BY
    void groupByCategory() throws SQLException ;

    // LIMIT
    Collection<Product> topNExpensiveProducts(int n) throws SQLException ;
}