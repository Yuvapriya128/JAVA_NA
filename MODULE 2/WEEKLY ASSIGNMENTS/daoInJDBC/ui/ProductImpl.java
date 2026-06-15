package daoInJDBC.ui;

import daoInJDBC.dao.ProductDao;
import daoInJDBC.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import daoInJDBC.Connection.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
/*
 * to create table ->use postgresql in cmd
    private int id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private double discount;
    private double rating;

 * */
public class ProductImpl implements ProductDao {
    public Product mapToProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getDouble(5),rs.getDouble(6),rs.getDouble(7));
    }
    @Override
    public int save(Product product) throws SQLException {

        Connection con = DBManager.getConnection();

        String sql =
                "insert into product(name,category,brand,price,discount,rating) " +
                        "values(?,?,?,?,?,?)";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, product.getName());
        stmt.setString(2, product.getCategory());
        stmt.setString(3, product.getBrand());
        stmt.setDouble(4, product.getPrice());
        stmt.setDouble(5, product.getDiscount());
        stmt.setDouble(6, product.getRating());

        int rows = stmt.executeUpdate();

        DBManager.closeConnection(con);

        return rows;
    }

    @Override
    public Collection<Product> findAll() throws SQLException {
        Connection con = DBManager.getConnection();
        List<Product> products = new LinkedList<>();
        String sql = "select * from product";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            products.add(mapToProduct(rs));
        }
        DBManager.closeConnection(con);
        return products;
    }

    @Override
    public Collection<Product> findByCategory(String category)
            throws SQLException {
        Connection con = DBManager.getConnection();
        List<Product> products = new LinkedList<>();
        String sql =
                "select * from product where category=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }
        DBManager.closeConnection(con);
        return products;
    }

    @Override
    public Collection<Product> findByPriceRange(
            double min,
            double max) throws SQLException {

        Connection con = DBManager.getConnection();

        List<Product> products = new LinkedList<>();

        String sql =
                "select * from product " +
                        "where price between ? and ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setDouble(1, min);
        stmt.setDouble(2, max);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }

        DBManager.closeConnection(con);

        return products;
    }

    @Override
    public Collection<Product> findByCategoryAndBrand(
            String category,
            String brand) throws SQLException {

        Connection con = DBManager.getConnection();

        List<Product> products = new LinkedList<>();

        String sql =
                "select * from product " +
                        "where category=? and brand=?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, category);
        stmt.setString(2, brand);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }

        DBManager.closeConnection(con);

        return products;
    }

    @Override
    public Collection<Product> sortByPrice()
            throws SQLException {

        Connection con = DBManager.getConnection();

        List<Product> products = new LinkedList<>();

        String sql =
                "select * from product order by price";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }

        DBManager.closeConnection(con);

        return products;
    }

    @Override
    public Collection<Product> sortByPriceAndRating()
            throws SQLException {

        Connection con = DBManager.getConnection();

        List<Product> products = new LinkedList<>();

        String sql =
                "select * from product " +
                        "order by price,rating";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }

        DBManager.closeConnection(con);

        return products;
    }

    @Override
    public double averagePrice()
            throws SQLException {

        double avg = 0;

        Connection con = DBManager.getConnection();

        String sql =
                "select avg(price) from product";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            avg = rs.getDouble(1);
        }

        DBManager.closeConnection(con);

        return avg;
    }

    @Override
    public void groupByCategory()
            throws SQLException {

        Connection con = DBManager.getConnection();

        String sql =
                "select category,count(*) as count " +
                        "from product " +
                        "group by category";

        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {

            System.out.println(
                    rs.getString(1)
                            + " "
                            + rs.getInt(2)
            );
        }

        DBManager.closeConnection(con);
    }

    @Override
    public Collection<Product> topNExpensiveProducts(int n)
            throws SQLException {

        Connection con = DBManager.getConnection();

        List<Product> products = new LinkedList<>();

        String sql =
                "select * from product " +
                        "order by price desc limit ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, n);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            products.add(mapToProduct(rs));
        }

        DBManager.closeConnection(con);

        return products;
    }
}
