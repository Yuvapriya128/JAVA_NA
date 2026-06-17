package bookSIOC.controller;

import bookSIOC.dao.BookDao;
import bookSIOC.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class BookConsoleController {
    private Scanner sc;
    private BookDao bookDao;

    @Autowired
    public BookConsoleController(Scanner sc, BookDao bookDao) {
        this.sc = sc;
        this.bookDao = bookDao;
    }

    public void welcome(){
        System.out.println("Welcome to Book controller");
    }
    public void showMenu(){
        System.out.println("Book menu");
        System.out.println("1.Add book");
        System.out.println("2.Find by id");
        System.out.println("3.Find All");
        System.out.println("4.update by id");
        System.out.println("5.delete by id");

        do{
            System.out.println("Enter option:");
            int option= sc.nextInt();
            redirectChoice(option);
        }while(true);
    }
    private void redirectChoice(int option){
        switch (option){
            case 1 -> {add();}
            case 2->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                System.out.println(bookDao.findBookById(id));
            }
            case 3->{bookDao.findAll().forEach(System.out::println);}
            case 4->{
                update();
            }
            case 5->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                bookDao.deleteBookById(id);
            }
            default -> {throw  new IllegalArgumentException("Invalid choice");}
        }
    }
    private void add(){
        System.out.println("Enter id");
        int id=sc.nextInt();
        System.out.println("Enter title");
        sc.nextLine();
        String title=sc.nextLine();
        System.out.println("Enter author");
        String author=sc.nextLine();
        System.out.println("Enter price");
        double price=sc.nextDouble();
        bookDao.addBook(new Book(id,title,author,price));
    }
    private void update(){
        System.out.println("Enter id");
        int id=sc.nextInt();
        System.out.println("Enter title");
        sc.nextLine();
        String title=sc.nextLine();
        System.out.println("Enter author");
        String author=sc.nextLine();
        System.out.println("Enter price");
        double price=sc.nextDouble();
        bookDao.updateBookById(id,new Book(id,title,author,price));
    }
}
