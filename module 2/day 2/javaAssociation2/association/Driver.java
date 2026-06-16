package JavaAssociation2.Association;

public class Driver {
    private String name;
    private int exp;

    public Driver(String name, int exp) {
        this.name = name;
        this.exp = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    public void drive(Car c){
        System.out.println("Driver drives "+c.getModel());
    }

    public static void main(String[] args) {
        Driver d1=new Driver("selvam",5);
        Car c1=new Car("BMW");
        Car c2=new Car("Toyota");
        d1.drive(c1);
        d1.drive(c2);

//        Car is not a (member variable/member function)
//        member of Driver but a parameter  and it's passed down
//        to Driver class  : This is basic association

    }
}
