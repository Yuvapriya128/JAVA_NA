package daoImpl.nbfcImpl;

import dao.nbfc.KYCVerification;
import entity.Customer;

public class AadharVerify implements KYCVerification {
    @Override
    public boolean verify(Customer customer) {
        if(customer.getAadhaarNumber().length()==16){
            return true;
        }
        else{
            return false;
        }
    }
}
