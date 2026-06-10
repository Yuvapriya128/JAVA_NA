package DirectAccessObject.dao;

import DirectAccessObject.entity.Book;

public interface Bookdao {
    public void save(Book book);
    public Book findById(int id);
    public void deleteById(int id);
    public void update(Book book);
    public void deleteAll();
    public Iterable<Book> findAll();
    public Iterable<Book> findByAuthor(String Author);
    public Iterable<Book> sortByTitleAsc();
    public Iterable<Book> sortByTitleDesc();
}
