package com.northernArc.bookmanagement.dao;

import com.northernArc.bookmanagement.model.Book;

import java.util.Collection;

public interface BookDao {
    void save(Book book);
    void deleteById(int id);
    Book findById(int id);
    Collection<Book> findAll();
    void updateById(int id,Book book);
}
