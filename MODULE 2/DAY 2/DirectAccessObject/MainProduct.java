package DirectAccessObject;

import DirectAccessObject.entity.Product;
import DirectAccessObject.ui.Productimpl;

import java.util.Scanner;

public class MainProduct {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Productimpl productdao = new Productimpl();

        System.out.println("========== PRODUCT MENU ==========");
        System.out.println("1 -> Save Product");
        System.out.println("2 -> Find Product By ID");
        System.out.println("3 -> Delete Product By ID");
        System.out.println("4 -> Update Product");
        System.out.println("5 -> Delete All Products");
        System.out.println("6 -> Display All Products");
        System.out.println("7 -> Find Products By Category");
        System.out.println("8 -> Find Products By Brand");
        System.out.println("9 -> Sort Products By Price Asc");
        System.out.println("10 -> Sort Products By Rating Desc");
        System.out.println("11 -> Exit");

        do {

            System.out.print("\nEnter option : ");

            int option = sc.nextInt();
            sc.nextLine();

            switch(option) {

                case 1:

                    System.out.println("Enter Product ID : ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Enter Product Name : ");
                    String name = sc.nextLine();

                    System.out.println("Enter Category : ");
                    String category = sc.nextLine();

                    System.out.println("Enter Brand : ");
                    String brand = sc.nextLine();

                    System.out.println("Enter Price : ");
                    double price = sc.nextDouble();

                    System.out.println("Enter Discount : ");
                    double discount = sc.nextDouble();

                    System.out.println("Enter Rating : ");
                    double rating = sc.nextDouble();

                    Product p1 = new Product(
                            id,
                            name,
                            category,
                            brand,
                            price,
                            discount,
                            rating
                    );

                    productdao.save(p1);

                    System.out.println("Product Saved Successfully");

                    break;

                case 2:

                    System.out.println("Enter Product ID : ");
                    int findId = sc.nextInt();

                    Product foundProduct =
                            productdao.findById(findId);

                    if(foundProduct != null) {

                        System.out.println(foundProduct);
                    }
                    else {

                        System.out.println("Product Not Found");
                    }

                    break;

                case 3:

                    System.out.println("Enter Product ID to delete : ");
                    int deleteId = sc.nextInt();

                    productdao.deleteById(deleteId);

                    System.out.println("Product Deleted Successfully");

                    break;

                case 4:

                    System.out.println("Enter Product ID to update : ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Enter Product Name : ");
                    String upName = sc.nextLine();

                    System.out.println("Enter Category : ");
                    String upCategory = sc.nextLine();

                    System.out.println("Enter Brand : ");
                    String upBrand = sc.nextLine();

                    System.out.println("Enter Price : ");
                    double upPrice = sc.nextDouble();

                    System.out.println("Enter Discount : ");
                    double upDiscount = sc.nextDouble();

                    System.out.println("Enter Rating : ");
                    double upRating = sc.nextDouble();

                    Product updatedProduct = new Product(
                            updateId,
                            upName,
                            upCategory,
                            upBrand,
                            upPrice,
                            upDiscount,
                            upRating
                    );

                    productdao.update(updatedProduct);

                    System.out.println("Product Updated Successfully");

                    break;

                case 5:

                    productdao.deleteAll();

                    System.out.println("All Products Deleted");

                    break;

                case 6:

                    Iterable<Product> allProducts =
                            productdao.findAll();

                    for(Product p : allProducts) {

                        System.out.println(p);
                    }

                    break;

                case 7:

                    System.out.println("Enter Category : ");
                    String searchCategory = sc.nextLine();

                    Iterable<Product> categoryProducts =
                            productdao.findByCategory(searchCategory);

                    for(Product p : categoryProducts) {

                        System.out.println(p);
                    }

                    break;

                case 8:

                    System.out.println("Enter Brand : ");
                    String searchBrand = sc.nextLine();

                    Iterable<Product> brandProducts =
                            productdao.findByBrand(searchBrand);

                    for(Product p : brandProducts) {

                        System.out.println(p);
                    }

                    break;

                case 9:

                    Iterable<Product> priceProducts =
                            productdao.sortByPriceAsc();

                    for(Product p : priceProducts) {

                        System.out.println(p);
                    }

                    break;

                case 10:

                    Iterable<Product> ratingProducts =
                            productdao.sortByRatingDesc();

                    for(Product p : ratingProducts) {

                        System.out.println(p);
                    }

                    break;

                case 11:

                    System.out.println("Exiting...");
                    System.exit(0);

                default:

                    System.out.println("Invalid Option");
            }

        } while(true);
    }
}