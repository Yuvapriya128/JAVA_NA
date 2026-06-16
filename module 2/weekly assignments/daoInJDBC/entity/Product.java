package daoInJDBC.entity;

public class Product {
    private int id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private double discount;
    private double rating;

    public Product(){}

    public Product(String name, String category, String brand, double price, double discount, double rating) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.discount = discount;
        this.rating = rating;
    }

    public Product(int id, String name, String category, String brand, double price, double discount, double rating) {
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
}

