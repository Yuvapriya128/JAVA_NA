package dao.nbfc;

import entity.Customer;

public interface KYCVerification {

    boolean verify(Customer customer);
}