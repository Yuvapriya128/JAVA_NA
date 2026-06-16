package daoInJDBC.ui;

import daoInJDBC.Connection.DBManager;
import daoInJDBC.dao.BookDao;
import daoInJDBC.entity.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
* to create table ->use postgresql in cmd

* */
public class Bookimpl implements BookDao {

    public Book mapToBook(ResultSet rs) throws SQLException{
        return new Book(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4));
    }

    @Override
    public int save(Book book)  {
        try {
            Connection con = DBManager.getConnection();
            String sql = "INSERT INTO book(title,author,publisher) values(?,?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            int rows = stmt.executeUpdate();
            DBManager.closeConnection(con);
            return rows;
        } catch (SQLException e) {
            System.out.println("Issue in db connectivity: "+e.getMessage());
        }
        return 0;
    }

    @Override
    public Book findById(int id) {
        try{
            Connection con=DBManager.getConnection();
            String sql="Select * from book where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,id);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()) {
               return mapToBook(rs);
            }
            DBManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Issue in db connectivity: "+e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<Book> findByTitle(String title) {
        List<Book> books=new LinkedList<>();
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book where title=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,title);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                books.add( mapToBook(rs) );
            }
        }catch (SQLException e){
            System.out.println("db connectivity issue "+e.getMessage());
        }
        return books;
    }

    @Override
    public Collection<Book> findSortedByAuthorAsc() {
        List<Book> books=new LinkedList<>();
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book order by author";
            PreparedStatement stmt=con.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                books.add( mapToBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }
        return books;
    }

    @Override
    public Collection<Book> findByTitleAndPublisher(String title, String Publisher) {
        List<Book> books=new LinkedList<>();
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book where title=? and publisher=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,title);
            stmt.setString(2,Publisher);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                books.add( mapToBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }
        return books;
    }

    @Override
    public Collection<Book> sortByTitle() {
        List<Book> books=new LinkedList<>();
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book order by title";
            PreparedStatement stmt=con.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                books.add( mapToBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }
        return books;
    }

    @Override
    public void groupByAuthor(String Author) {

        try(Connection con=DBManager.getConnection()){
            String sql="select author,count(*) from book where author=? group by author";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,Author);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("author")+" ->"+rs.getInt(2));
            }

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }

    }

    @Override
    public void deleteById(int id) {
        try(Connection con=DBManager.getConnection()){
            String sql="delete from book where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,id);

            int rows=stmt.executeUpdate();
            if(rows>0){
                System.out.println("Deleted successfully");
            }else{
                System.out.println("No book found with id:"+id);
            }


        } catch (Exception e) {
            System.out.println("db connectivity issue "+e.getMessage());
        }

    }

    @Override
    public Collection<Book> findAll() {
        List<Book> books=new LinkedList<>();
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book";
            PreparedStatement stmt=con.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                books.add( mapToBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }
        return books;
    }

    @Override
    public void deleteAll() {
        try(Connection con=DBManager.getConnection()){
            String sql="delete from book";
//            truncate resets id
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("db connectivity issue "+e.getMessage());
        }

    }

    @Override
    public void updateById(int id,String title) {

        try(Connection con=DBManager.getConnection()){
            String sql="update book set title=? where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,title);
            stmt.setInt(2,id);
            int rows=stmt.executeUpdate();
            System.out.println("Rows updated:"+rows);

        } catch (SQLException e) {
            System.out.println("db connectivity issue"+e);
        }

    }

    @Override
    public void existsById(int id) {
        int row=0;
        try(Connection con=DBManager.getConnection()){
            String sql="select * from book where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Book exists");
            }else{
                System.out.println("Book not found");
            }


        } catch (Exception e) {
            System.out.println("db connectivity issue "+e.getMessage());
        }

    }
}
