package com.demo.restapidemo.service;

import com.demo.restapidemo.dao.BookDao;
import com.demo.restapidemo.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService{
    @Autowired
    private BookDao bookDao;

    @Override
    public Book saveBook(Book book) {
//        we will validate here
        return bookDao.save(book);
    }

    @Override
    public void deleteBook(int id) {
// validate conditions here
        bookDao.delete(id);
    }

    @Override
    public Book getById(int id) {
        return bookDao.findById(id);
    }

    @Override
    public void updateById(int id, Book b) {
         bookDao.updateById(id,b);
    }

    @Override
    public List<Book> getAll() {
        return bookDao.findAll();
    }
}
