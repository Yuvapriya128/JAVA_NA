package JavaAssociation2.Aggregation;

public class MainUl {
    public static void main(String[] args) {


        UnderWriter uw1 = new UnderWriter("Rahul", 500000);
        UnderWriter uw2 = new UnderWriter("Priya", 300000);


        LoanApplications l1 =
                new LoanApplications(200000, 5, 7.5, "Pending", 101);

        LoanApplications l2 =
                new LoanApplications(450000, 10, 8.2, "Approved", 102);

        LoanApplications l3 =
                new LoanApplications(600000, 15, 9.0, "Pending", 103);

        LoanApplications l4 =
                new LoanApplications(150000, 3, 6.8, "Approved", 104);


        uw1.addApplications(l1);
        uw1.addApplications(l2);
        uw1.addApplications(l3); // exceeds limit


        uw2.addApplications(l4);
        uw2.addApplications(l1);


        System.out.println("Applications handled by " + uw1.getName());
        for (LoanApplications app : uw1.showApplications()) {
            System.out.println(app);
        }

        System.out.println();


        System.out.println("Applications handled by " + uw2.getName());
        for (LoanApplications app : uw2.showApplications()) {
            System.out.println(app);
        }

        System.out.println();

        // Removing one application
        uw1.removeApplications(101);

        System.out.println("After removing customer 101 from "
                + uw1.getName());

        for (LoanApplications app : uw1.showApplications()) {
            System.out.println(app);
        }
    }
}