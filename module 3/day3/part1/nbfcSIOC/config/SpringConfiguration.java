package nbfcSIOC.config;


import nbfcSIOC.connection.DBManager;
import nbfcSIOC.controller.BorrowConsoleController;
import nbfcSIOC.dao.BorrowDao;
import nbfcSIOC.daoImpl.BorrowDaoImplJdbc;
import nbfcSIOC.entity.Borrow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@org.springframework.context.annotation.Configuration
public class SpringConfiguration {

    @Bean
    public Scanner sc(){
        return new Scanner(System.in);
    }
//    jdbc
    @Bean
    public BorrowDao create2(@Qualifier("nbfcdb") DBManager d){
        return new BorrowDaoImplJdbc(d);
    }

    @Bean
    public BorrowConsoleController create3(Scanner sc, BorrowDao bd){
        return new BorrowConsoleController(sc,bd);

    }
    @Bean("nbfcdb")
    public DBManager createdb(){
        return new DBManager("jdbc:postgresql://localhost:5432/northernarc",
                "postgres","12345");
    }

}
