package com.northernArc.flightmanagement.controller;

import com.northernArc.flightmanagement.dao.FlightDao;
import com.northernArc.flightmanagement.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@Component
public class FlightConsoleController {
    @Autowired
    public Scanner sc;

    @Autowired
    public FlightDao flightDao;

    public void showMenu(){
        System.out.println("Welcome to Flight");
        System.out.println("1.add");
        System.out.println("2.Find by ID");
        System.out.println("3.Find All");
        System.out.println("4.update by No");
        System.out.println("5.delete by No");
        System.out.println("6.search flight");
        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            sc.nextLine();
            redirectChoice(option);

        }while(true);
    }

    void redirectChoice(int n) {
        switch (n) {
            case 1 -> {
                flightDao.save(read());
            }
            case 2 -> {
                System.out.println("Enter flight no");
                String no=sc.nextLine();
                System.out.println(flightDao.findByNo(no));

            }
            case 3 -> {
                flightDao.findAll().forEach(System.out::println);

            }
            case 4 -> {
                System.out.println("Enter flightno");
                String flightno=sc.nextLine();

                flightDao.updateByNo(flightno,read());
            }
            case 5 -> {
                System.out.println("Enter flight no");
                String no=sc.nextLine();
                flightDao.deleteByNo(no);

            }

            case 6->{
                System.out.println("Enter source");
                String src = sc.nextLine();
                System.out.println("Enter destination");
                String dest=sc.nextLine();
                DateTimeFormatter timeFormatter =
                        DateTimeFormatter.ofPattern("HH:mm");
                Date date=Date.valueOf(LocalDate.now());
                Time time= Time.valueOf(LocalTime.now().withNano(0));
                flightDao.findBySrcDestAndDepDate(src,dest,date,time).forEach(System.out::println);

            }
            default -> {
                System.out.println("Invalid choice");
            }
        }
    }

    private Flight read(){
        System.out.println("Enter flightNo");
        String flightno = sc.nextLine();
        System.out.println("Enter source");

        String src = sc.nextLine();
        System.out.println("Enter destination");
        String dest=sc.nextLine();

        System.out.println("Enter cost per seat");
        Double cost = sc.nextDouble();
        System.out.println("No. of seat");
        int nos=sc.nextInt();
        return new Flight(flightno,src,dest,cost,nos);

    }

}
