
// package oop.encapsulation;

public class Flight {
    private String airline;
    private String flightNo;
    private String source;
    private String dest;
    private String departureDateTime;
    private String arrivalDateTime;

    public Flight(String airline,String flightNo,String source,String dest,String departureDateTime,String arrivalDateTime){
        this.airline=airline;
        this.flightNo=flightNo;
        this.source=source;
        this.dest=dest;
        this.departureDateTime=departureDateTime;
        this.arrivalDateTime=arrivalDateTime;

    }
    public String getter(){
        return (airline+"-"+flightNo+"-"+source+"-"+dest+"-["+departureDateTime+"-"+arrivalDateTime+"]");
    }

    public void getStatus(){
       System.out.println(airline+" "+arrivalDateTime);
    }
    public void showDetails(){
      System.out.println(airline+"-"+flightNo+"-"+source+"-"+dest+"-["+departureDateTime+"-"+arrivalDateTime+"]");
    }
}
