package daoImpl.nbfcImpl;

import dao.nbfc.KYCVerification;
import entity.Customer;

public class PanVerify implements KYCVerification {
    @Override
    public boolean verify(Customer customer) {
        if(customer.getPanNumber().length()==10){
            return true;
        }else {
            return false;
        }
    }
}
