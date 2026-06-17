package controller;

import dao.ProductDao;
import entity.Product;

import java.sql.SQLException;
import java.util.Scanner;

public class ProdConsoleController {
    private Scanner sc;
    private ProductDao productDao;

    public ProdConsoleController(Scanner sc, ProductDao productDao) {
        this.sc = sc;
        this.productDao = productDao;
    }

    public void welcome(){
        System.out.println("Welcome to Product controller");
    }

    public void showMenu() throws SQLException {
        System.out.println("1.save ");
        System.out.println("2.findAll ");
        System.out.println("3.findByPriceRange ");
        System.out.println("4.sortByPrice ");
        System.out.println("5.deleteById ");

        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectMenu(option);
        }while(true);
    }

    private void redirectMenu(int option) throws SQLException {
        switch (option){
            case 1 -> addProduct();

            case 2 -> productDao.findAll()
                    .forEach(System.out::println);

            case 3->{findByPriceRange();}
            case 4 -> productDao.sortByPrice()
                    .forEach(System.out::println);
            case 5-> {System.out.println("Enter name:");
                int id=sc.nextInt();
                productDao.deleteById(id);}

            default -> {throw new IllegalArgumentException("Invalid choice");}
        }

    }
     void addProduct() throws SQLException {

        sc.nextLine();

        System.out.println("Enter name:");
        String name=sc.nextLine();

        System.out.println("Enter category:");
        String category=sc.nextLine();

        System.out.println("Enter brand:");
        String brand=sc.nextLine();

        System.out.println("Enter price:");
        double price=sc.nextDouble();

        System.out.println("Enter discount:");
        double discount=sc.nextDouble();

        System.out.println("Enter rating:");
        double rating=sc.nextDouble();

        Product product=new Product(
                name,
                category,
                brand,
                price,
                discount,
                rating
        );

        productDao.save(product);
    }
     void findByPriceRange() throws SQLException {

        System.out.println("Enter minimum price:");
        double min=sc.nextDouble();

        System.out.println("Enter maximum price:");
        double max=sc.nextDouble();

        productDao.findByPriceRange(min,max)
                .forEach(System.out::println);
    }
}
