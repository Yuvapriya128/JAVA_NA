package com.demo.restapidemo.dao;

import com.demo.restapidemo.entity.Book;

import java.util.List;
import java.util.Map;

public interface BookDao {
    Book save(Book b);
    void delete(int id);
    Book findById(int id);
    List<Book> findAll();
    void updateById(int id,Book b);
}
