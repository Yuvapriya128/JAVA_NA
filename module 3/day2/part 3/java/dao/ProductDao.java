package dao;


import entity.Product;

import java.sql.SQLException;
import java.util.Collection;

public interface ProductDao {


    void save(Product product) throws SQLException;

    Collection<Product> findAll() throws SQLException ;

    Collection<Product> findByPriceRange(
            double min,
            double max) throws SQLException ;


    Collection<Product> sortByPrice() throws SQLException ;

    void deleteById(int id) throws SQLException;

}