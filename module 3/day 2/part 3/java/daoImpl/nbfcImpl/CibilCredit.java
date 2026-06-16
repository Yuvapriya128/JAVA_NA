package daoImpl.nbfcImpl;

import dao.nbfc.CreditScoreService;
import entity.Customer;

public class CibilCredit implements CreditScoreService {
    @Override
    public int fetchCreditScore(Customer customer) {
        int cs=customer.getCreditScore()-100;

        return cs;
    }
}
