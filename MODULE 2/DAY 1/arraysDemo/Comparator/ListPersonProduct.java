package arraysDemo.Comparator;

import arraysDemo.PersonComparable;

import java.util.*;

public class ListPersonProduct {
    public static void main(String[] args) {
//do person and product in list (ArrayList, LinkedList)
        System.out.println("PERSON");
        System.out.println("-------------\nArraylist\n-----------");

        List<PersonComparator> listperson=new ArrayList<>();
        listperson.add(new PersonComparator("siva","nantham",51));
        listperson.add(new PersonComparator("Ravi", "Kumar", 25));
        listperson.add(new PersonComparator("Priya", "Sharma", 30));
        listperson.add(new PersonComparator("Arun", "Prakash", 45));
        listperson.add(new PersonComparator("Meena", "Lakshmi", 28));

        System.out.println(listperson);

        listperson.remove(0);

        System.out.println(listperson);

        Scanner sc=new Scanner(System.in);
        System.out.println("Enter 1-> age 2-> fname 3-> lname sorting:");
        int n=sc.nextInt();

        switch (n){
            case 1:
                System.out.println("Sort by age");
                listperson.sort(new AgeComparator());
                break;
            case 2:
                System.out.println("Sort by fname");
                listperson.sort(new FnameComparator());
                break;
            case 3:
                System.out.println("Sort by lname");
                listperson.sort(new LnameComparator());
                break;
        }

        System.out.println(listperson);

        System.out.println("-------------\nLinkedlist\n-----------");


        List<PersonComparator> llperson=new LinkedList<>();
        llperson.add(new PersonComparator("siva","nantham",51));
        llperson.add(new PersonComparator("Ravi", "Kumar", 25));
        llperson.add(new PersonComparator("Priya", "Sharma", 30));
        llperson.add(new PersonComparator("Arun", "Prakash", 45));
        llperson.add(new PersonComparator("Meena", "Lakshmi", 28));

        System.out.println(llperson);

        llperson.remove(0);

        System.out.println(llperson);


        System.out.println("Enter 1-> age 2-> fname 3-> lname sorting:");
        int lln=sc.nextInt();

        switch (lln){
            case 1:
                System.out.println("Sort by age");
                llperson.sort(new AgeComparator());
                break;
            case 2:
                System.out.println("Sort by fname");
                llperson.sort(new FnameComparator());
                break;
            case 3:
                System.out.println("Sort by lname");
                llperson.sort(new LnameComparator());
                break;
        }

        System.out.println(llperson);

        System.out.println("\n\n----------------\n\n");
        System.out.println("PRODUCT");

        class comparerating implements Comparator<ProductComparator> {
            @Override
            public int compare(ProductComparator p1, ProductComparator p2) {
                return Double.compare(p1.getRating() , p2.getRating());
            }
        }
        class comparecategorydes implements Comparator<ProductComparator>{
            @Override
            public int compare(ProductComparator p1, ProductComparator p2){
                return p2.getCategory().compareToIgnoreCase(p1.getCategory());
            }

        }
        class comparebranddes implements Comparator<ProductComparator>{
            @Override
            public int compare(ProductComparator p1, ProductComparator p2){
                return p2.getBrand().compareToIgnoreCase(p1.getBrand());
            }
        }
        class comparepricedes implements Comparator<ProductComparator>{@Override
        public int compare(ProductComparator p1, ProductComparator p2){
            return Double.compare(p2.getPrice(),p1.getPrice());
        }

        }
        class comparediscountdes implements Comparator<ProductComparator>{
            @Override
            public int compare(ProductComparator p1, ProductComparator p2){
                return Double.compare(p2.getDiscount(),p1.getDiscount());
            }

        }
        class compareratingdes implements Comparator<ProductComparator> {
            @Override
            public int compare(ProductComparator p1, ProductComparator p2) {
                return Double.compare(p2.getRating() , p1.getRating());
            }
        }

        List<ProductComparator> products = new ArrayList<>();

        products.add(new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5));
        products.add(new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3));
        products.add(new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6));
        products.add(new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1));

        System.out.println("6. Category Desc");
        System.out.println("7. Price Desc");
        System.out.println("8. Discount Desc");
        System.out.println("9. Rating Desc");
        System.out.println("10. Brand Desc");

        int choice = sc.nextInt();

        switch (choice) {

            case 6:
                System.out.println("Category Desc");
                products.sort(new comparecategorydes());
                break;

            case 7:
                System.out.println("Price Desc");
                products.sort(new comparepricedes());
                break;

            case 8:
                System.out.println("Discount Desc");
                products.sort(new comparediscountdes());
                break;

            case 9:
                System.out.println("Rating Desc");
                products.sort(new compareratingdes());
                break;

            case 10:
                System.out.println("Brand Desc");
                products.sort(new comparebranddes());
                break;

            default:
                System.out.println("Invalid Choice");
        }

        System.out.println(products);

        System.out.println("\n\n-----------\n\n");
        LinkedList<ProductComparator> productslinked = new LinkedList<>();

        productslinked.add(new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5));
        productslinked.add(new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3));
        productslinked.add(new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6));
        productslinked.add(new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1));

        System.out.println("6. Category Desc");
        System.out.println("7. Price Desc");
        System.out.println("8. Discount Desc");
        System.out.println("9. Rating Desc");
        System.out.println("10. Brand Desc");

        int choicel = sc.nextInt();

        switch (choicel) {

            case 6:
                System.out.println("Category Desc");
                productslinked.sort(new comparecategorydes());
                break;

            case 7:
                System.out.println("Price Desc");
                productslinked.sort(new comparepricedes());
                break;

            case 8:
                System.out.println("Discount Desc");
                productslinked.sort(new comparediscountdes());
                break;

            case 9:
                System.out.println("Rating Desc");
                productslinked.sort(new compareratingdes());
                break;

            case 10:
                System.out.println("Brand Desc");
                productslinked.sort(new comparebranddes());
                break;

            default:
                System.out.println("Invalid Choice");
        }

        System.out.println(productslinked);

    }
}
