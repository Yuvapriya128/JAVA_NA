package daoInJDBC.dao;

import daoInJDBC.entity.Book;

import java.sql.SQLException;
import java.util.Collection;

//add in all methods:throws SQLException

public interface BookDao {
    public int save(Book b);
    public Book findById(int id);
    public Collection<Book> findByTitle(String title);
    public Collection<Book> findSortedByAuthorAsc();
    public Collection<Book> findByTitleAndPublisher(String title,String Publisher);
    public Collection<Book> sortByTitle();
    public void groupByAuthor(String Author);
    public void  deleteById(int id);
    public Collection<Book> findAll();
    public void deleteAll();
    public void updateById(int id,String title);
    public void existsById(int id);
}
