package DirectAccessObject.dao;

import DirectAccessObject.entity.Product;

public interface Productdao {

    public void save(Product product);
    public Product findById(int id);
    public void deleteById(int id);
    public void update(Product product);
    public void deleteAll();
    public Iterable<Product> findAll();
    public Iterable<Product> findByCategory(String category);
    public Iterable<Product> findByBrand(String brand);
    public Iterable<Product> sortByPriceAsc();
    public Iterable<Product> sortByRatingDesc();
}