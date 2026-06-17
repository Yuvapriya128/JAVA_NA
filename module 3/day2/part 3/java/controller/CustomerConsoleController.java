package controller;

import entity.Customer;
import service.CustomerRegistrationManager;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerConsoleController {
    private final Scanner sc;
    private final CustomerRegistrationManager customerManager;

    public CustomerConsoleController(Scanner sc, CustomerRegistrationManager customerManager) {
        this.sc = sc;
        this.customerManager = customerManager;
    }

    public void welcome(){
        System.out.println("Welcome to Customer Console Controller");
    }

    public void showMenu() throws SQLException {
        System.out.println("===== Customer Onboarding System =====");
        System.out.println("\n1. Register Customer");
        System.out.println("2. Find Customer By Id");
        System.out.println("3. Find All Customers");
        System.out.println("4. Update Customer");
        System.out.println("5. Delete Customer");
        System.out.println("6. Exit");
        do {
            System.out.print("Enter Option : ");
            int option = sc.nextInt();
            switch (option) {
                case 1 -> registerCustomer();
                case 2 -> findCustomerById();
                case 3 -> findAllCustomers();
                case 4 -> updateCustomer();
                case 5 -> deleteCustomer();
                case 6 -> {
                    System.out.println("Thank You");
                    return;
                }
                default -> System.out.println("Invalid Option");
            }
        } while (true);
    }

    private void registerCustomer() throws SQLException {
        System.out.println("Enter Customer Id");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Customer Name");
        String name = sc.nextLine();
        System.out.println("Enter Mobile");
        String mobile = sc.nextLine();
        System.out.println("Enter PAN Number");
        String pan = sc.nextLine();
        System.out.println("Enter Aadhaar Number");
        String aadhaar = sc.nextLine();
        Customer customer = new Customer(id, name, mobile, pan, aadhaar, 0);
        customerManager.registerCustomer(customer);
    }

    private void findCustomerById() throws SQLException {
        System.out.println("Enter Customer Id");
        int id = sc.nextInt();
        Customer customer = customerManager.findCustomerById(id);
        System.out.println(customer);
    }

    private void findAllCustomers() throws SQLException {
        customerManager.findAllCustomers().forEach(System.out::println);
    }

    private void updateCustomer() throws SQLException {
        System.out.println("Enter Customer Id");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Customer Name");
        String name = sc.nextLine();
        System.out.println("Enter Mobile");
        String mobile = sc.nextLine();
        System.out.println("Enter PAN Number");
        String pan = sc.nextLine();
        System.out.println("Enter Aadhaar Number");
        String aadhaar = sc.nextLine();
        System.out.println("Enter Credit Score");
        int creditScore = sc.nextInt();
        Customer customer = new Customer(id, name, mobile, pan, aadhaar, creditScore);
        customerManager.updateCustomer(id, customer);
    }

    private void deleteCustomer() throws SQLException {
        System.out.println("Enter Customer Id");
        int id = sc.nextInt();
        customerManager.deleteCustomer(id);
    }
}
