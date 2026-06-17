package bookSIOC.dao;

import bookSIOC.entity.Book;

import java.util.Collection;

public interface BookDao {
    void addBook(Book book);
    Book findBookById(int id);
    void updateBookById(int id, Book book);
    Collection<Book> findAll();
    void deleteBookById(int id);

}
