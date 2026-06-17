package flightSIOC.config;

import flightSIOC.controller.FlightConsoleController;
import flightSIOC.dao.FlightDao;
import flightSIOC.daoImpl.FlightDaoImplJdbc;
import flightSIOC.connection.DBManager;
import flightSIOC.service.FlightService;
import flightSIOC.service.FlightServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class FlightConfig {
    @Bean
    public Scanner sc(){
        return new Scanner(System.in);
    }

    @Bean("flightdb")
    public DBManager invent(){
        return new DBManager("jdbc:postgresql://localhost:5432/northernarc",
                "postgres","12345");
    }
    @Bean
    public FlightDao invent1(@Qualifier("flightdb") DBManager dbManager){
        return new FlightDaoImplJdbc(dbManager);
    }
    @Bean
    public FlightService invent3(FlightDao flightDao){
        return new FlightServiceImpl(flightDao);
    }
    @Bean
    public FlightConsoleController invent4(Scanner sc, FlightService flightService){
        return new FlightConsoleController(sc,flightService);
    }
}
