package dao;

import entity.Customer;

import java.sql.SQLException;
import java.util.Collection;

public interface CustomerDao {

    void save(Customer customer) throws SQLException;

    Customer findById(int id) throws SQLException;

    Collection<Customer> findAll() throws SQLException;

    void deleteById(int id) throws SQLException;

    void updateById(int id, Customer customer) throws SQLException;
}