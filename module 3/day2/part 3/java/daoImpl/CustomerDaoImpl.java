package daoImpl;
import dao.CustomerDao;
import entity.Customer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class CustomerDaoImpl implements CustomerDao {

    Set<Customer> customerSet = new LinkedHashSet<>();

    @Override
    public void save(Customer customer) {
        customerSet.add(customer);
    }

    @Override
    public Customer findById(int id) {

        for(Customer customer : customerSet) {

            if(customer.getId() == id) {
                return customer;
            }
        }

        return null;
    }

    @Override
    public Collection<Customer> findAll() {
        return customerSet;
    }

    @Override
    public void deleteById(int id) {

        Customer customer = findById(id);

        if(customer != null) {
            customerSet.remove(customer);
        }
    }

    @Override
    public void updateById(int id, Customer customer) {

        deleteById(id);

        customerSet.add(customer);

        System.out.println("Customer Updated");
    }
}