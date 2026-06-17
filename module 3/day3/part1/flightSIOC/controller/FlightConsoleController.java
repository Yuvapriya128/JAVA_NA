package flightSIOC.controller;

import flightSIOC.dao.FlightDao;
import flightSIOC.entity.Flight;
import flightSIOC.service.FlightService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Scanner;

public class FlightConsoleController {
    private final Scanner sc;

//    variable and constructor changed to service
    private FlightService flightDao;

    public FlightConsoleController(Scanner sc, FlightService flightDao) {
        this.sc = sc;
        this.flightDao = flightDao;
    }
    public void welcome(){
        System.out.println("Flight console controller");
    }
    public void showMenu(){
        System.out.println("Flight Menu");
        System.out.println("-------------");
        System.out.println("1.Add Flight");
        System.out.println("2.Find by No");
        System.out.println("3.Find All");
        System.out.println("4.update by No");
        System.out.println("5.delete by No");
        System.out.println("6.Find By src,dest,departure date,time");

        do{
            System.out.println("Enter option:");
            int n=sc.nextInt();
            redirectChoice(n);
        }while(true);
    }

    private Flight readFlight(){

        System.out.println("Enter flightNo");
        String fno=sc.nextLine();
        System.out.println("Enter source");
        String src=sc.nextLine();

        System.out.println("Enter destination");
        String dest=sc.nextLine();

        System.out.println("Enter cost per seat");
        double cps=sc.nextDouble();
        System.out.println("Enter no. of seat");
        int nos=sc.nextInt();
        System.out.println("Enter date of departure(yyyy-mm-dd)");
        LocalDate dod=LocalDate.parse(sc.next());
        System.out.println("Enter date of arrival");
        LocalDate doa=LocalDate.parse(sc.next());
        System.out.println("Enter time of departure(HH:mm)");
        LocalTime tod= LocalTime.parse(sc.next());
        System.out.println("Enter time of arrival");
        LocalTime toa=LocalTime.parse(sc.next());
        Flight flight=new Flight(fno,src,dest,cps,nos,dod,doa,tod,toa);
        return flight;
    }

    private void redirectChoice(int n){
        switch (n){
            case 1 -> flightDao.addFlight(readFlight());
            case 2->{
                System.out.println("Enter FlightNo.");
                String id=sc.next();
                System.out.println(flightDao.getFlight(id));
            }
            case 3->{
                flightDao.getAllFlights().forEach(System.out::println);
            }
            case 4 -> {
                System.out.println("Enter flightNo");
                String id = sc.next();
                flightDao.updateFlight(id, readFlight());
            }

            case 5->{
                System.out.println("Enter FlightNo.");
                String id=sc.next();
                flightDao.deleteFlight(id);
            }
            case 6->{
                System.out.println("Enter source");
                String src=sc.nextLine();
                System.out.println("Enter destination");
                String dest=sc.nextLine();
                System.out.println("Enter date of departure(yyyy-mm-dd)");
                LocalDate dod=LocalDate.parse(sc.next());
                System.out.println("Enter time of departure(HH:mm)");
                LocalTime tod= LocalTime.parse(sc.next());

                flightDao.searchFlight(src,dest,dod,tod).forEach(System.out::println);
            }
            default -> {
                System.out.println("Invalid choice");
                return;
            }
        }
    }
}
