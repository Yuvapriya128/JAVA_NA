package config;

import connection.DBManager;
import controller.CustomerConsoleController;
import controller.ProdConsoleController;
import controller.TodoConsoleController;
import dao.CustomerDao;
import dao.ProductDao;
import dao.TodoDao;
import dao.nbfc.CreditScoreService;
import dao.nbfc.KYCVerification;
import dao.nbfc.NotificationService;
import daoImpl.*;
import daoImpl.nbfcImpl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.CustomerRegistrationManager;

import java.util.Scanner;

@Configuration
public class TodoSpringConfig {

//    Todo

    @Bean("todo")
    public TodoDao create(){
        return new TodoDaoImpl();
    }
    @Bean("todojdbc")
    public TodoDao create2(DBManager dbManager){
        return new TodoDaoImplJdbc(dbManager);
    }
    @Bean
    public TodoConsoleController consoleController(Scanner sc,@Qualifier("todo") TodoDao todoDao){
        return new TodoConsoleController(sc,todoDao);
    }

//    Product
    @Bean("product")
    public ProductDao create3(){
        return new ProdDaoImpl();
    }

    @Bean("productjdbc")
    public ProductDao create4(DBManager dbManager){
        return new ProdDaoImplJdbc(dbManager);
    }
    @Bean("prodcontroller")
    public ProdConsoleController consoleController2(Scanner sc,@Qualifier("product")ProductDao productDao){
        return new ProdConsoleController(sc,productDao);
    }
    @Bean("prodcontrollerjdbc")
    public ProdConsoleController consoleController3(Scanner sc,@Qualifier("productjdbc")ProductDao productDao){
        return new ProdConsoleController(sc,productDao);
    }

//    Customer

    @Bean("pan")
    public KYCVerification panVerify() {
        return new PanVerify();
    }

    @Bean("aadhaar")
    public KYCVerification aadhaarVerify() {
        return new AadharVerify();
    }

    @Bean("cibil")
    public CreditScoreService cibilCredit() {
        return new CibilCredit();
    }

    @Bean("experian")
    public CreditScoreService experianCredit() {
        return new ExperianCredit();
    }

    @Bean("sms")
    public NotificationService smsNotify() {
        return new SmsNotify();
    }

    @Bean("email")
    public NotificationService emailNotify() {
        return new EmailNotify();
    }

    @Bean("customer")
    public CustomerDao create5(){
        return  new CustomerDaoImpl();
    }
    @Bean("customerjdbc")
    public CustomerDao create6(DBManager dbManager){
        return new CustomerDaoImplJdbc(dbManager);
    }
    @Bean("customermanager")
    public CustomerRegistrationManager customerManager(
            @Qualifier("customer") CustomerDao customerDao,
            @Qualifier("pan") KYCVerification kycService,
            @Qualifier("cibil") CreditScoreService creditScoreService,
            @Qualifier("sms") NotificationService notificationService) {

        return new CustomerRegistrationManager(
                customerDao,
                kycService,
                creditScoreService,
                notificationService);
    }

    @Bean("custcontroller")
    public CustomerConsoleController customerConsoleController(Scanner sc,@Qualifier("customermanager")CustomerRegistrationManager custController){
        return new CustomerConsoleController(sc,custController);
    }
    @Bean("customermanagerjdbc")
    public CustomerRegistrationManager customerManagerJdbc(
            @Qualifier("customerjdbc") CustomerDao customerDao,
            @Qualifier("pan") KYCVerification kycService,
            @Qualifier("cibil") CreditScoreService creditScoreService,
            @Qualifier("sms") NotificationService notificationService) {

        return new CustomerRegistrationManager(
                customerDao,
                kycService,
                creditScoreService,
                notificationService);
    }

    @Bean("custcontrollerjdbc")
    public CustomerConsoleController customerConsoleControllerJdbc(
            Scanner sc,
            @Qualifier("customermanagerjdbc")
            CustomerRegistrationManager customerManager) {

        return new CustomerConsoleController(
                sc,
                customerManager);
    }


    @Bean
    public Scanner scanner(){
        return new Scanner(System.in);
    }

    @Bean
    public DBManager getDbManager(){
        return new DBManager(
                "jdbc:postgresql://localhost:5432/northernarc",
                "postgres",
                "12345");
    }


}
