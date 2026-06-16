package MyCollections2.Interconversion;

import java.util.*;

public class setListPerson {
    public static void main(String[] args) {
        Set<Person> personSet=new LinkedHashSet<>();

        personSet.add(new Person("mahir", "Rahul",22));
        personSet.add(new Person("meera", "Priya",21));
        personSet.add(new Person("sita", "Arun",20));


        System.out.println("List -> set");
        List<Person> employeeList=new ArrayList<>();

        employeeList.add(new Person("mahir", "Rahul", 22));
        employeeList.add(new Person("meera", "Priya", 21));
        employeeList.add(new Person("sita", "Arun", 20));
        employeeList.add(new Person("sita", "Arun", 20));

//        LinkedHashSet removes duplicates only if
//        equals() and hashCode() are overridden in the Person class.

        System.out.println(employeeList);

        Set<Person> employeeSet = new LinkedHashSet<>(employeeList);
        employeeList=new LinkedList<>(employeeSet);

        System.out.println(employeeSet);



    }
}
