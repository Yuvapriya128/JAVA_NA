package daoInJDBC.Main;

import daoInJDBC.dao.ProductDao;
import daoInJDBC.entity.Product;
import daoInJDBC.ui.ProductImpl;

import java.sql.SQLException;
import java.util.Scanner;

public class ProductMain {

    private static Scanner sc=new Scanner(System.in);
    private static ProductDao productDao=new ProductImpl();

    public static void main(String[] args) throws SQLException {

        System.out.println("===== PRODUCT MENU =====");
        System.out.println("1. Add Product");
        System.out.println("2. Find All Products");
        System.out.println("3. Find By Category");
        System.out.println("4. Find By Price Range");
        System.out.println("5. Find By Category And Brand");
        System.out.println("6. Sort By Price");
        System.out.println("7. Sort By Price And Rating");
        System.out.println("8. Average Price");
        System.out.println("9. Group By Category");
        System.out.println("10. Top N Expensive Products");
        System.out.println("0. Exit");

        do{

            System.out.println("Enter option:");
            int option=sc.nextInt();

            switch(option){

                case 1 -> addProduct();

                case 2 -> productDao.findAll()
                        .forEach(System.out::println);

                case 3 -> findByCategory();

                case 4 -> findByPriceRange();

                case 5 -> findByCategoryAndBrand();

                case 6 -> productDao.sortByPrice()
                        .forEach(System.out::println);

                case 7 -> productDao.sortByPriceAndRating()
                        .forEach(System.out::println);

                case 8 -> System.out.println(
                        productDao.averagePrice()
                );

                case 9 -> productDao.groupByCategory();

                case 10 -> topNProducts();

                case 0 -> System.exit(0);

                default -> System.out.println("Invalid option");
            }

        }while(true);
    }

    static void addProduct() throws SQLException {

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

        int rows=productDao.save(product);

        System.out.println("Rows inserted:"+rows);
    }

    static void findByCategory() throws SQLException {

        sc.nextLine();

        System.out.println("Enter category:");
        String category=sc.nextLine();

        productDao.findByCategory(category)
                .forEach(System.out::println);
    }

    static void findByPriceRange() throws SQLException {

        System.out.println("Enter minimum price:");
        double min=sc.nextDouble();

        System.out.println("Enter maximum price:");
        double max=sc.nextDouble();

        productDao.findByPriceRange(min,max)
                .forEach(System.out::println);
    }

    static void findByCategoryAndBrand() throws SQLException {

        sc.nextLine();

        System.out.println("Enter category:");
        String category=sc.nextLine();

        System.out.println("Enter brand:");
        String brand=sc.nextLine();

        productDao.findByCategoryAndBrand(
                category,
                brand
        ).forEach(System.out::println);
    }

    static void topNProducts() throws SQLException {

        System.out.println("Enter N:");

        int n=sc.nextInt();

        productDao.topNExpensiveProducts(n)
                .forEach(System.out::println);
    }
}