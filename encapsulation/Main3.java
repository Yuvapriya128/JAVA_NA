// package oop.encapsulation;

public class Main3 {
    public static void main(String[] args) {

        System.out.println("   ---  Superhero   ---   ");

        Superhero sh=new Superhero();
        sh.setter("spidy","web" );
        sh.useSuperpower();
        sh.saveWorld();

        System.out.println("   ---  Normal Person   ---   ");

        Normal np=new Normal("jackie", "chan", 50);
        np.getter();
        np.eat();
        np.walk();
        np.talk();

        System.out.println("   ---  Book   ---   ");

        Book bk=new Book("Mahabharat", "Valmiki", 10000);
        bk.getter();
        bk.read();
        bk.getSummary();

        System.out.println("   ---  SuperVillain   ---   ");

        Supervillain sv=new Supervillain("Thanos","destroying world", "5 stones");
        sv.getter();
        sv.jailname();

        System.out.println("   ---  Employee   ---   ");

        Employee emp=new Employee("jack", "Manager", 50000);
        emp.getter();
        emp.work();
        emp.getDetails();

        System.out.println("   ---  Movie   ---   ");

        Movie mv=new Movie("3","Anonymous",2010);
        mv.getter();
        mv.play();
        mv.getRating();

        System.out.println("   ---  Student   ---   ");

        Student st=new Student("priya", "O", "Antony's matr. school");
        st.getClass();
        st.getReportCard();
        st.getter();

        System.out.println("   ---  Car   ---   ");

        Car cr=new Car("New model","Indigo",2005);
        cr.start();
        cr.stop();
        cr.getDetails();

        System.out.println("   ---  Flight   ---   ");
        
        Flight fl=new Flight("Air India", "AF2345", "chennai", "Banglore", "3.6.2026 11.10 A.M.","3.6.2026 11.40 A.M.");
        fl.getter();
        fl.getStatus();
        fl.showDetails();

        System.out.println("   ---  Chair   ---   ");

        Chair ch=new Chair("Polymer","Orange",3);
        ch.adjustHeight();
        ch.getDetails();

        System.out.println("   ---  Artist   ---   ");

        Artist ar=new Artist("priya","oil paint","canvas");
        ar.create();
        ar.getDetails();

        System.out.println("   ---  Athlete   ---   ");
        Athlete ath=new Athlete("jack","cricket","csk");
        ath.train();
        ath.compete();
        ath.getDetails();

        System.out.println("   ---  Doctor   ---   ");
        Doctor dr=new Doctor("sundar",5,"neurologist");
        dr.diagnose();
        dr.getter();

        System.out.println("   ---  Hotel   ---   ");
        Hotel hl=new Hotel("dhans","chennai","4");
        hl.bookRoom();
        hl.getter();


        System.out.println("   ---  Laptop   ---   ");
        Laptop lp=new Laptop("dell","15 C","15GB","16GB RAM","500 GB");
        lp.turnOn();
        lp.turnOff();
        lp.getDetails();

        // System.out.println("   ---  LightBulb   ---   ");
        // LightBulb lb=new LightBulb(5,"viloc");
        // lb.turnOn();
        // lb.turnOff();
        // lb.getDetails();

        System.out.println("   ---  Restaurant   ---   ");
        Restaurant rt=new Restaurant("dhans res","chinese",4);
        rt.orderFood();
        rt.getDetails();

        System.out.println("   ---  Teacher   ---   ");
        Teacher tr=new Teacher("radha","tamil",4);
        tr.teach();
        tr.getDetails();

    }
}
