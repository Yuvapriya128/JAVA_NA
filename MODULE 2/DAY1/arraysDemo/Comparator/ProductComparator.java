package arraysDemo.Comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class ProductComparator {
    private int id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private double discount;
    private double rating;

    public ProductComparator(int id, String name, String category, String brand, double price, double discount, double rating) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.discount = discount;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString(){
        return ("\n"+id+" "+name+" "+category+" "+brand+" "+price+" "+discount+" "+rating);
    }


    public static void main(String[] args) {

        System.out.println("1. Category Asc");
        System.out.println("2. Price Asc");
        System.out.println("3. Discount Asc");
        System.out.println("4. Rating Asc");
        System.out.println("5. Brand Asc");
        System.out.println("6. Category Desc");
        System.out.println("7. Price Desc");
        System.out.println("8. Discount Desc");
        System.out.println("9. Rating Desc");
        System.out.println("10. Brand Desc");
        System.out.println("11. Swap products");
        System.out.println("12. Reverse order");


        class comparerating implements Comparator<ProductComparator> {
            @Override
            public int compare(ProductComparator p1, ProductComparator p2) {
                return Double.compare(p1.rating , p2.rating);
            }
        }
            class comparecategorydes implements Comparator<ProductComparator>{
                @Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return p2.category.compareToIgnoreCase(p1.category);
                }

            }
            class comparebranddes implements Comparator<ProductComparator>{
                @Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return p2.brand.compareToIgnoreCase(p1.brand);
                }
            }
            class comparepricedes implements Comparator<ProductComparator>{@Override
            public int compare(ProductComparator p1, ProductComparator p2){
                return Double.compare(p2.price,p1.price);
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
                    return Double.compare(p2.rating , p1.rating);
                }
            }

                ProductComparator[] products = {
                        new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5),
                        new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3),
                        new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6),
                        new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1),
                        new ProductComparator(105, "Air Conditioner", "Electronics", "Daikin", 55000, 15, 4.4),
                        new ProductComparator(106, "Television", "Electronics", "Sony", 80000, 18, 4.7),
                        new ProductComparator(107, "Keyboard", "Accessories", "Logitech", 1500, 20, 4.0),
                        new ProductComparator(108, "Mouse", "Accessories", "HP", 700, 10, 3.9),
                        new ProductComparator(109, "Printer", "Office", "Canon", 12000, 7, 4.2),
                        new ProductComparator(115, "Microwave", "Home Appliances", "IFB", 18000, 14, 4.2)
                };

        System.out.println(Arrays.toString(products));

        System.out.println("Enter options:");
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        switch (n){
//            1 - 5 is anonymous class

            case 1:
                Arrays.sort(products,new Comparator<ProductComparator>(){
                @Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return p1.category.compareToIgnoreCase(p2.category);
                }

            });
                System.out.println(Arrays.toString(products));
                break;
            case 2:
                Arrays.sort(products,new Comparator<ProductComparator>(){@Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return Double.compare(p1.price,p2.price);
                }

            });
                System.out.println(Arrays.toString(products));
                break;
            case 3:
                Arrays.sort(products,new Comparator<ProductComparator>(){
                @Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return Double.compare(p1.discount,p2.discount);
                }

            });
                System.out.println(Arrays.toString(products));
                break;
            case 4:
                Arrays.sort(products,new Comparator<ProductComparator>() {
                @Override
                public int compare(ProductComparator p1, ProductComparator p2) {
                    return Double.compare(p1.rating , p2.rating);
                }
            });
                System.out.println(Arrays.toString(products));
                break;
            case 5:
                Arrays.sort(products,new Comparator<ProductComparator>(){
                @Override
                public int compare(ProductComparator p1, ProductComparator p2){
                    return p1.brand.compareToIgnoreCase(p2.brand);
                }
            });
                System.out.println(Arrays.toString(products));
                break;

//                6-10 is inner local class

            case 6:
                Arrays.sort(products,new comparecategorydes());
                System.out.println(Arrays.toString(products));
                break;
            case 7:
                Arrays.sort(products,new comparepricedes());
                System.out.println(Arrays.toString(products));
                break;
            case 8:
                Arrays.sort(products,new comparediscountdes());
                System.out.println(Arrays.toString(products));
                break;
            case 9:
                Arrays.sort(products,new compareratingdes());
                System.out.println(Arrays.toString(products));
                break;
            case 10:
                Arrays.sort(products,new comparebranddes());
                System.out.println(Arrays.toString(products));
                break;
            case 11:
                System.out.println("Enter first product id:");
                int fid=sc.nextInt();
                System.out.println("Enter second product id:");
                int sid=sc.nextInt();
                ProductComparator temp=products[fid];
                products[fid]=products[sid];
                products[sid]=temp;

                System.out.println(Arrays.toString(products));

                break;
            case 12:
                System.out.println("To reverse:");
                for(int i=0;i<products.length/2;i++){
                    ProductComparator tem=products[i];
                    products[i]=products[products.length-i-1];
                    products[products.length-i-1]=tem;
                }

                System.out.println(Arrays.toString(products));
                break;

        }
}
}
