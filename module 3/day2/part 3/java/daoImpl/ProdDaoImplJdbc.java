package daoImpl;

import connection.DBManager;
import dao.ProductDao;
import entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ProdDaoImplJdbc implements ProductDao {
    private final DBManager dbManager;

    public ProdDaoImplJdbc(DBManager dbManager){
        this.dbManager=dbManager;
    }
    public Product mapToProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getDouble(5),rs.getDouble(6),rs.getDouble(7));
    }

    @Override
    public void save(Product product) throws SQLException {
        Connection con = dbManager.getConnection();
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
        dbManager.closeConnection(con);
        System.out.println("Rows inserted:"+rows);
    }

    @Override
    public Collection<Product> findAll() throws SQLException {
        Connection con = dbManager.getConnection();
        List<Product> products = new LinkedList<>();
        String sql = "select * from product";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            products.add(mapToProduct(rs));
        }
        dbManager.closeConnection(con);
        return products;
    }

    @Override
    public Collection<Product> findByPriceRange(double min, double max) throws SQLException {
        Connection con = dbManager.getConnection();
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
        dbManager.closeConnection(con);
        return products;
    }

    @Override
    public Collection<Product> sortByPrice() throws SQLException {
        Connection con = dbManager.getConnection();
        List<Product> products = new LinkedList<>();
        String sql =
                "select * from product order by price";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            products.add(mapToProduct(rs));
        }
        dbManager.closeConnection(con);
        return products;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        Connection con= dbManager.getConnection();
        String sql="delete from product where id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,id);
        int row=stmt.executeUpdate();
        System.out.println("Rows deleted:"+row);

        con.close();

    }
}
