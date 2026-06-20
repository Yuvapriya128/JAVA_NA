package com.demo.restapidemo.service;

import com.demo.restapidemo.entity.Book;

import java.util.List;

public interface BookService {
    Book saveBook(Book book);
    void deleteBook(int id);
    Book getById(int id);
    void updateById(int id,Book b);
    List<Book> getAll();

}
