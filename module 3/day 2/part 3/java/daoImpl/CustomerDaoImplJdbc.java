package daoImpl;

import connection.DBManager;
import dao.CustomerDao;
import entity.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerDaoImplJdbc implements CustomerDao {

    private final DBManager dbManager;

    public CustomerDaoImplJdbc(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private Customer mapToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getInt(6)
        );
    }

    @Override
    public void save(Customer customer) throws SQLException {
        String sql = "insert into customer values(?,?,?,?,?,?)";
        try (Connection con = dbManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, customer.getId());
            stmt.setString(2, customer.getCustomerName());
            stmt.setString(3, customer.getMobile());
            stmt.setString(4, customer.getPanNumber());
            stmt.setString(5, customer.getAadhaarNumber());
            stmt.setInt(6, customer.getCreditScore());
            int row = stmt.executeUpdate();
            System.out.println("Rows Inserted : " + row);
        }
    }

    @Override
    public Customer findById(int id) throws SQLException {
        String sql = "select * from customer where customer_id=?";
        try (Connection con = dbManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapToCustomer(rs);
        }
        return null;
    }

    @Override
    public Collection<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "select * from customer";
        try (Connection con = dbManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) customers.add(mapToCustomer(rs));
        }
        return customers;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "delete from customer where customer_id=?";
        try (Connection con = dbManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int row = stmt.executeUpdate();
            System.out.println("Rows Deleted : " + row);
        }
    }

    @Override
    public void updateById(int id, Customer customer) throws SQLException {
        String sql = """
                update customer
                set customer_name=?,
                    mobile=?,
                    pan_number=?,
                    aadhaar_number=?,
                    credit_score=?
                where customer_id=?
                """;
        try (Connection con = dbManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getMobile());
            stmt.setString(3, customer.getPanNumber());
            stmt.setString(4, customer.getAadhaarNumber());
            stmt.setInt(5, customer.getCreditScore());
            stmt.setInt(6, id);
            int row = stmt.executeUpdate();
            System.out.println("Rows Updated : " + row);
        }
    }
}