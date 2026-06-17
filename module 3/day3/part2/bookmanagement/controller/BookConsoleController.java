package com.northernArc.bookmanagement.controller;

import com.northernArc.bookmanagement.dao.BookDao;
import com.northernArc.bookmanagement.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class BookConsoleController {
    @Autowired
    public Scanner sc;
    @Autowired
    public BookDao bookDao;

    public void showMenu(){
        System.out.println("Welcome to Book");
        System.out.println("1.add");
        System.out.println("2.Find by ID");
        System.out.println("3.Find All");
        System.out.println("4.update by No");
        System.out.println("5.delete by No");
        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectChoice(option);

        }while(true);
    }

    void redirectChoice(int n) {
        switch (n) {
            case 1 -> {
                System.out.println("Enter id");
                int id = sc.nextInt();
                System.out.println("Enter title");
                sc.nextLine();
                String title = sc.nextLine();
                System.out.println("Enter author");
                String author=sc.nextLine();
                System.out.println("Enter publisher");
                String publisher=sc.nextLine();
                System.out.println("Enter price");
                Double price = sc.nextDouble();
                bookDao.save(new Book(id, title, author,publisher,price));
            }
            case 2 -> {
                System.out.println("Enter id");
                int id = sc.nextInt();
                System.out.println(bookDao.findById(id));

            }
            case 3 -> {
                bookDao.findAll().forEach(System.out::println);

            }
            case 4 -> {
                System.out.println("Enter id");
                int id = sc.nextInt();
                System.out.println("Enter title");
                sc.nextLine();
                String title = sc.nextLine();
                System.out.println("Enter author");
                String author=sc.nextLine();
                System.out.println("Enter publisher");
                String publisher=sc.nextLine();
                System.out.println("Enter price");
                Double price = sc.nextDouble();
                bookDao.updateById(id,new Book(id, title, author,publisher,price));
            }
            case 5 -> {
                System.out.println("Enter id");
                int id = sc.nextInt();
                bookDao.deleteById(id);

            }
            default -> {
                System.out.println("Invalid choice");
            }
        }
    }

}
