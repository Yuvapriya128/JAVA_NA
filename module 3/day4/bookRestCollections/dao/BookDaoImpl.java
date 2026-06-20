package com.demo.restapidemo.dao;

import com.demo.restapidemo.entity.Book;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookDaoImpl implements BookDao{
    Map<Integer,Book> bookMap;

    @PostConstruct
    public void init(){
        bookMap=new LinkedHashMap<>();
        bookMap.put(1,new Book(1,"mind me","mohan","globe publisher",2000));
        bookMap.put(2,new Book(2,"Grids","sam","gm publisher",1800));
    }


    @Override
    public Book save(Book b) {
       return bookMap.put(b.getId(),b);
    }

    @Override
    public void delete(int id) {
       bookMap.remove(id);
    }

    @Override
    public Book findById(int id) {
        return bookMap.get(id);
    }

    @Override
    public List<Book> findAll() {
        return bookMap.values().stream().toList();
    }

    @Override
    public void updateById(int id, Book b) {
       bookMap.put(id,b);
    }

    @PreDestroy
    public void clear(){
        bookMap.clear();
    }
}
