package bookSIOC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "bookSIOC")
public class BookConfig {
    @Bean
    public Scanner create3(){
        return new Scanner(System.in);
    }

//    @Bean
//    public BookDao create(){
//        return new BookDaoImpl();
//    }
//    @Bean
//    public BookConsoleController create2(Scanner sc, BookDao bookDao){
//        return new BookConsoleController(sc,bookDao);
//    }
}
