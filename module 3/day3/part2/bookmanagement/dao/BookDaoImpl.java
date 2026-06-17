package com.northernArc.bookmanagement.dao;

import com.northernArc.bookmanagement.model.Book;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BookDaoImpl implements BookDao{



    List<Book> bookList=new ArrayList<>();

    @PostConstruct
    public void init(){
        System.out.println("PostConstruct");
        bookList.add(new Book(1,"On cloud nine","ME","Myself",900));
        bookList.add(new Book(2,"Once in a blue moon","Moony","Shine publisher",9089));
    }
    @PreDestroy
    public void clear(){
        bookList.clear();
    }

    @Override
    public void save(Book book) {
        bookList.add(book);

    }

    @Override
    public void deleteById(int id) {
        Book temp=null;
        for(Book b:bookList){
            if(b.getId()==id){
                temp=b;
            }
        }
        bookList.remove(temp);
    }

    @Override
    public Book findById(int id) {
        for(Book b:bookList){
            if(b.getId()==id){
                return b;
            }
        }
        return null;
    }

    @Override
    public Collection<Book> findAll() {
        return bookList;
    }

    @Override
    public void updateById(int id,Book book) {
        for(Book b:bookList){
            if(b.getId()==id){
                b.setAuthor(book.getAuthor());
                b.setPrice(book.getPrice());
                b.setPublisher(book.getPublisher());
                b.setTitle(book.getPublisher());
            }
        }
    }
}
