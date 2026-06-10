package DirectAccessObject.ui;

import DirectAccessObject.dao.Productdao;
import DirectAccessObject.entity.Product;

import java.util.*;

public class Productimpl implements Productdao {

    List<Product> products = new ArrayList<>();

    @Override
    public void save(Product product) {
        products.add(product);
    }

    @Override
    public Product findById(int id) {

        for(Product p : products) {

            if(p.getId() == id) {
                return p;
            }
        }

        return null;
    }

    @Override
    public void deleteById(int id) {

        Iterator<Product> productItr = products.iterator();

        while(productItr.hasNext()) {

            Product ptemp = productItr.next();

            if(ptemp.getId() == id) {
                productItr.remove();
            }
        }
    }

    @Override
    public void update(Product product) {

        for(Product p : products) {

            if(p.getId() == product.getId()) {

                p.setName(product.getName());
                p.setCategory(product.getCategory());
                p.setBrand(product.getBrand());
                p.setPrice(product.getPrice());
                p.setDiscount(product.getDiscount());
                p.setRating(product.getRating());
            }
        }
    }

    @Override
    public void deleteAll() {
        products.clear();
    }

    @Override
    public Iterable<Product> findAll() {
        return products;
    }

    @Override
    public Iterable<Product> findByCategory(String category) {

        List<Product> categoryProducts = new ArrayList<>();

        for(Product p : products) {

            if(p.getCategory().equalsIgnoreCase(category)) {
                categoryProducts.add(p);
            }
        }

        return categoryProducts;
    }

    @Override
    public Iterable<Product> findByBrand(String brand) {

        List<Product> brandProducts = new ArrayList<>();

        for(Product p : products) {

            if(p.getBrand().equalsIgnoreCase(brand)) {
                brandProducts.add(p);
            }
        }

        return brandProducts;
    }
    @Override
    public Iterable<Product> findByName(String name){
        List<Product> productListName=new LinkedList<>();
        for(Product p:products){
            if(p.getName().equalsIgnoreCase(name)){
                productListName.add(p);
            }
        }
        return  productListName;
    }

    @Override
    public Iterable<Product> sortByPriceAsc() {

        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {

                return Double.compare(p1.getPrice(), p2.getPrice());
            }
        });

        return products;
    }

    @Override
    public Iterable<Product> sortByRatingDesc() {

        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {

                return Double.compare(p2.getRating(), p1.getRating());
            }
        });

        return products;
    }
}
