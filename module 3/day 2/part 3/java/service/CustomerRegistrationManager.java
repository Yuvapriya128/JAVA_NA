package service;
import dao.CustomerDao;
import entity.Customer;
import dao.nbfc.CreditScoreService;
import dao.nbfc.KYCVerification;
import dao.nbfc.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import java.sql.SQLException;
import java.util.Collection;

public class CustomerRegistrationManager {
    private final CustomerDao customerDao;
    private final KYCVerification kycService;
    private final CreditScoreService creditScoreService;
    private final NotificationService notificationService;

    public CustomerRegistrationManager(CustomerDao customerDao,
                                       KYCVerification kycService,
                                       CreditScoreService creditScoreService,
                                       NotificationService notificationService) {
        this.customerDao = customerDao;
        this.kycService = kycService;
        this.creditScoreService = creditScoreService;
        this.notificationService = notificationService;
    }

    public void registerCustomer(Customer customer) throws SQLException {
        boolean verified = kycService.verify(customer);
        if (!verified) {
            System.out.println("KYC Verification Failed");
            return;
        }
        int creditScore = creditScoreService.fetchCreditScore(customer);
        customer.setCreditScore(creditScore);
        customerDao.save(customer);
        notificationService.sendMessage("Customer Registered Successfully");
        System.out.println("Customer Registration Successful");
    }

    public Customer findCustomerById(int id) throws SQLException {
        return customerDao.findById(id);
    }

    public Collection<Customer> findAllCustomers() throws SQLException {
        return customerDao.findAll();
    }

    public void updateCustomer(int id, Customer customer) throws SQLException {
        customerDao.updateById(id, customer);
        notificationService.sendMessage("Customer Updated Successfully");
    }

    public void deleteCustomer(int id) throws SQLException {
        customerDao.deleteById(id);
        notificationService.sendMessage("Customer Deleted Successfully");
    }
}