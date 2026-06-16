import java.util.Scanner;

public class selective2 {
    public static void main(String[] args) {
       Scanner sc=new Scanner(System.in);
       
       System.out.println("Enter month:");
       int month=sc.nextInt();

       System.out.println("Enter date");
       int date=sc.nextInt();

       int[] m={31,28,31,30,31,30,31,31,30,31,30,31};
       if(month<1 || month>12 || date<1 || date>31){
        System.out.println("invalid");  
       }else if(date>m[month-1]){
        System.out.println("invalid");  
       }else{
        int totaldays=0;
        for(int i=0;i<month-1;i++){
            totaldays+=m[i];
        }
        int modval=(totaldays+date)%7;
        switch(modval){
            case 5:
                System.out.println("MONDAY");
                break;
            case 6:
                System.out.println("TUESDAY");
                break;
            case 0:
                System.out.println("WEDNESDAY");
                break;
            case 1:
                System.out.println("THURSDAY");
                break;
            case 2:
                System.out.println("FRIDAY");
                break;
            case 3:
                 System.out.println("SATURDAY");
                break;
            case 4:
                System.out.println("SUNDAY");
                break;
            default:
                System.out.println("invalid");

            }


        }

    }
}
    
