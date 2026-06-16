package DirectAccessObject.ui;

import DirectAccessObject.dao.Bookdao;
import DirectAccessObject.entity.Book;

import java.util.*;

public class Bookimpl implements Bookdao {
    List<Book> books=new ArrayList<>();

    @Override
    public void save(Book book) {
        books.add(book);

    }

    @Override
    public Book findById(int isbn) {
        for(Book b:books){
            if(b.getIsbn()==isbn){
                return b;
            }
        }
        return null; //not found returns null
    }

    @Override
    public void deleteById(int id) {
        /*
        modifying in for each loop gives concurrentModificationError
        * Modifying while iterating uses -> Iterator
        that has remove , forEachRemaining
        *
        * */
        Iterator<Book> bookItr=books.iterator();
        while(bookItr.hasNext()){
            Book btemp=bookItr.next();
            if(btemp.getIsbn()==id){
                bookItr.remove();  //no object in iterator
            }
        }

    }

    @Override
    public void update(Book book) {
        for(Book b:books){
            if(b.getIsbn()==book.getIsbn()){
                b.setAuthor(book.getAuthor());
                b.setTitle(book.getAuthor());

            }
        }

    }

    @Override
    public void deleteAll() {
        books.clear();

    }

    @Override
    public Iterable<Book> findAll() {
        return books;
    }

    @Override
    public Iterable<Book> findByAuthor(String Author) {
        List<Book> authorbooks=new ArrayList<>();
        for(Book b:books){
            if(b.getAuthor().equalsIgnoreCase(Author)){
                authorbooks.add(b);
            }
        }
        return authorbooks;
    }

    @Override
    public Iterable<Book> sortByTitleAsc() {
        Collections.sort(books,new Comparator<Book>(){
                @Override
                public int compare(Book b1,Book b2){
                    return b1.getTitle().compareToIgnoreCase(b2.getTitle());
        }}
        );
        return books;
    }

    @Override
    public Iterable<Book> sortByTitleDesc() {
        Collections.sort(books,new Comparator<Book>(){
            @Override
            public int compare(Book b1,Book b2){
                return b2.getTitle().compareToIgnoreCase(b1.getTitle());
            }}
        );
        return books;
    }
}
