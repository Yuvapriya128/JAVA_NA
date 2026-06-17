package daoImpl;

import dao.ProductDao;
import entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ProdDaoImpl implements ProductDao {

    List<Product> productList=new LinkedList<>();



    @Override
    public void save(Product product) throws SQLException {
       productList.add(product);
    }

    @Override
    public Collection<Product> findAll() throws SQLException {
        return productList;
    }


    @Override
    public Collection<Product> findByPriceRange(double min, double max) throws SQLException {
        List<Product> result = new ArrayList<>();

        for (Product product : productList) {
            if (product.getPrice() >= min &&
                    product.getPrice() <= max) {

                result.add(product);
            }
        }

        return result;
    }


    @Override
    public Collection<Product> sortByPrice() throws SQLException {
       Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return (int)(o1.getPrice()-o2.getPrice());
            }
        });
       return productList;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        Product temp=null;
        for(Product p:productList){
            if(p.getId()==id){
                temp=p;
            }
        }
        productList.remove(temp);

    }
}
