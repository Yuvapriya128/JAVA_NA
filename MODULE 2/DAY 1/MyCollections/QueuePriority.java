package MyCollections;

import java.util.*;

import arraysDemo.Comparator.*;

public class QueuePriority {
    public static void main(String[] args) {
        System.out.println("Priority Queue - Integer");
        Queue<Integer> pq=new PriorityQueue<>();
        pq.add(3);
        pq.add(2);
        pq.add(1);
        System.out.println(pq);
        pq.remove(1);
        System.out.println(pq);
        pq.remove(2);
        System.out.println(pq);

//        Descending queue
        System.out.println("Priority Queue - Integer reversed order");

        Queue<Integer> pq2=new PriorityQueue<>(Collections.reverseOrder());
        pq2.add(3);
        pq2.add(2);
        pq2.add(1);
        System.out.println(pq2);
        pq2.remove(2);
        System.out.println(pq2.peek());
        System.out.println(pq2);
        pq2.remove(0); //no error
        System.out.println(pq2);

//        String queue
        System.out.println("Priority Queue - String");
        Queue<String> stringQueue=new PriorityQueue<>();
        stringQueue.add("apple");
        stringQueue.add("zee");
        stringQueue.add("yuva");
        System.out.println(stringQueue);
        stringQueue.remove("zee");
        System.out.println(stringQueue);

        //        Person
        System.out.println("Priority Queue - Object (Person)");
        Queue<PersonComparator> queueperson=new PriorityQueue<>(new AgeComparator().reversed());
        queueperson.add(new PersonComparator("gokul","S",19));
        queueperson.add(new PersonComparator("yuva","S",21));
        queueperson.add(new PersonComparator("siva","nantham",51));
        queueperson.add(new PersonComparator("amudha","S",41));

        System.out.println("Removing "+queueperson.remove()); //fifo
        //System.out.println(queueperson);   -> not needed
        System.out.println("Removing "+queueperson.remove()); //fifo
        System.out.println(queueperson);

//        person with user input
        Scanner sc=new Scanner(System.in);

            System.out.println("Enter age,fname,lname(1(age),2(fname),3(lname)):");
            int option=sc.nextInt();
        Queue<PersonComparator> queuePersonUser=null;
            switch (option) {

                case 1:
                    queuePersonUser=new PriorityQueue<>(new AgeComparator());
                    queuePersonUser.add(new PersonComparator("Siva","Nantham",51));
                    queuePersonUser.add(new PersonComparator("Ravi","Kumar",25));
                    queuePersonUser.add(new PersonComparator("Priya","Sharma",30));
                    System.out.println(queuePersonUser);
                    System.out.println("Removing "+queuePersonUser.remove());
                    System.out.println(queuePersonUser);

                    break;
                case 2:
                    queuePersonUser=new PriorityQueue<>(new FnameComparator());
                    queuePersonUser.add(new PersonComparator("Siva","Nantham",51));
                    queuePersonUser.add(new PersonComparator("Ravi","Kumar",25));
                    queuePersonUser.add(new PersonComparator("Priya","Sharma",30));
                    System.out.println(queuePersonUser);
                    System.out.println("Removing "+queuePersonUser.remove());
                    System.out.println(queuePersonUser);

                    break;
                case 3:
                    queuePersonUser=new PriorityQueue<>(new LnameComparator());
                    queuePersonUser.add(new PersonComparator("Siva","Nantham",51));
                    queuePersonUser.add(new PersonComparator("Ravi","Kumar",25));
                    queuePersonUser.add(new PersonComparator("Priya","Sharma",30));
                    System.out.println(queuePersonUser);
                    System.out.println("Removing "+queuePersonUser.remove());
                    System.out.println(queuePersonUser);
                    break;

            }


//            product with user input , anonymous class
        System.out.println("\n\n--------product with user input-------");

        System.out.println("Enter 1(Price), 2(Discount), 3(Category):");
        int option2 = sc.nextInt();

        Queue<ProductComparator> queueProductUser = null;

        switch (option2) {

            case 1:
                queueProductUser = new PriorityQueue<>(new Comparator<ProductComparator>() {
                    @Override
                    public int compare(ProductComparator p1, ProductComparator p2) {
                        return Double.compare(p1.getPrice() , p2.getPrice());
                    }
                });

                queueProductUser.add(new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5));
                queueProductUser.add(new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3));
                queueProductUser.add(new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6));
                queueProductUser.add(new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1));

                System.out.println(queueProductUser);
                System.out.println("Removing " + queueProductUser.remove());
                System.out.println(queueProductUser);
                break;

            case 2:
                queueProductUser = new PriorityQueue<>(new Comparator<ProductComparator>() {
                    @Override
                    public int compare(ProductComparator p1, ProductComparator p2) {
                        return Double.compare(p1.getDiscount() , p2.getDiscount());
                    }
                });

                queueProductUser.add(new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5));
                queueProductUser.add(new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3));
                queueProductUser.add(new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6));
                queueProductUser.add(new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1));

                System.out.println(queueProductUser);
                System.out.println("Removing " + queueProductUser.remove());
                System.out.println(queueProductUser);
                break;

            case 3:
                queueProductUser = new PriorityQueue<>(new Comparator<ProductComparator>() {
                    @Override
                    public int compare(ProductComparator p1, ProductComparator p2) {
                        return p1.getCategory().compareToIgnoreCase( p2.getCategory());
                    }
                });

                queueProductUser.add(new ProductComparator(101, "Laptop", "Electronics", "Dell", 75000, 10, 4.5));
                queueProductUser.add(new ProductComparator(102, "Mobile", "Electronics", "Samsung", 45000, 5, 4.3));
                queueProductUser.add(new ProductComparator(103, "Refrigerator", "Home Appliances", "LG", 65000, 12, 4.6));
                queueProductUser.add(new ProductComparator(104, "Washing Machine", "Home Appliances", "Whirlpool", 40000, 8, 4.1));

                System.out.println(queueProductUser);
                System.out.println("Removing " + queueProductUser.remove());
                System.out.println(queueProductUser);
                break;

            default:
                System.out.println("Invalid Choice");
        }






    }
}
