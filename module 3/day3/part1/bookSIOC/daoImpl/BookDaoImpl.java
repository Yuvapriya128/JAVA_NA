package bookSIOC.daoImpl;

import bookSIOC.dao.BookDao;
import bookSIOC.entity.Book;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class BookDaoImpl implements BookDao {
    Set<Book> bookSet=new LinkedHashSet<>();
    @Override
    public void addBook(Book book) {
        bookSet.add(book);
    }

    @Override
    public Book findBookById(int id) {
        for(Book b:bookSet){
            if(b.getId()==id){
                return b;
            }
        }
        return null;
    }

    @Override
    public void updateBookById(int id, Book book) {

        for(Book b:bookSet){
            if(b.getId()==id){
                b.setAuthor(book.getAuthor());
                b.setTitle(book.getTitle());
                b.setPrice(book.getPrice());
            }
        }


    }

    @Override
    public Collection<Book> findAll() {
        return bookSet;
    }

    @Override
    public void deleteBookById(int id) {
        Book temp=null;
        for(Book b:bookSet){
            if(b.getId()==id){
                temp=b;
            }
        }
        bookSet.remove(temp);

    }
}
