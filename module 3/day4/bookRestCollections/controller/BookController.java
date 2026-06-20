package com.demo.restapidemo.controller;

import com.demo.restapidemo.dao.BookDao;
import com.demo.restapidemo.entity.Book;
import com.demo.restapidemo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/books")
public class BookController {
//    @Autowired
//    private BookService bookService;
//
//    @GetMapping
//    public List<Book> findAll(){
//        return bookService.getAll();
//    }
//
//    @GetMapping("/{id}")
//    public Book getByid(@PathVariable  int id){
//       return bookService.getById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delById(@PathVariable int id){
//        bookService.deleteBook(id);
//    }
//
//    @PostMapping
//    public void save(@RequestBody Book book){
//        bookService.saveBook(book);
//    }
//
//    @PutMapping("/{id}")
//    public void update(@PathVariable int id,@RequestBody Book book){
//        bookService.updateById(id,book);
//    }



    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> findAll(){
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getByid(@PathVariable  int id){
//        return RensponseEntity.ok(bookService.getById(id));
        return new ResponseEntity<>(bookService.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delById(@PathVariable int id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Book> save(@RequestBody Book book){
        return ResponseEntity.status(201).body(bookService.saveBook(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable int id,@RequestBody Book book){
        bookService.updateById(id,book);
        return ResponseEntity.ok(book);
    }


}
