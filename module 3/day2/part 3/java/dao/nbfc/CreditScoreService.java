package dao.nbfc;

import entity.Customer;

public interface CreditScoreService {

    int fetchCreditScore(Customer customer);
}