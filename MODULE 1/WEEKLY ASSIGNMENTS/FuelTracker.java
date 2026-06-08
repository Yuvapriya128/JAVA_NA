package covariant;


import java.util.Scanner;

public class FuelTracker {
    protected double fuelcapacity;
    protected double curfuel;
    public FuelTracker(double fuelcapacity,double curfuel){
        this.fuelcapacity=fuelcapacity;
        this.curfuel=curfuel;
    }
    public void fill(double amt){
        if(amt+curfuel > fuelcapacity){
            System.out.println("Cannot fill. Capacity is reached");
        }else{
            curfuel+=amt;
        }
    }
//    expected km
//    2km/l
    public void drive (double km){
        if(km<0){
            System.out.println("Distance cannot be negative");
            return;
        }
        if(curfuel <=0){
            System.out.println("Fuel is empty");
            return;
        }
        double totalkm=curfuel*4;
        double temp=totalkm;


        if(totalkm-km <0){
            System.out.println("You can drive only kms: "+totalkm);
        }else{
            System.out.println("Fuel is reduced");
            curfuel-=km/2;
        }
    }
    public void checkfuel(){
        System.out.println("Remaining balance: "+curfuel);
    }

    public static void main(String[] args) {
        System.out.println("Enter Fuel Capacity:");
        Scanner sc=new Scanner(System.in);
        double fuelCapacity=sc.nextDouble();

        System.out.println("Enter current fuel balance:");
        double curfuelbal=sc.nextDouble();

        FuelTracker ba=new FuelTracker(fuelCapacity,curfuelbal);

        do{
            System.out.println("Enter 1(fill),2(drive),3(check),4(exit)");
            int option=sc.nextInt();
            switch (option){
                case 1:
                    System.out.println("Enter amount to fill:");
                    double fuel=sc.nextDouble();
                    ba.fill(fuel);
                    break;
                case 2:
                    System.out.println("Enter km to drive:");
                    double with=sc.nextDouble();
                    ba.drive(with);
                    break;
                case 3:
                    ba.checkfuel();
                    break;
                case 4:
                    return;
            }

        }while(true);
    }
}

