package MyCollections2.Interconversion;

import java.util.*;

public class ListQueuePerson {

    public static void main(String[] args) {

        // List -> Queue
        System.out.println("List -> Queue");

        List<Person> personList = new ArrayList<>();

        personList.add(new Person("mahir", "Rahul", 22));
        personList.add(new Person("meera", "Priya", 21));
        personList.add(new Person("sita", "Arun", 20));

        System.out.println(personList);

        Queue<Person> personQueue = new LinkedList<>(personList);

        System.out.println(personQueue);

        System.out.println("\n----------------------\n");

        // Queue -> List
        System.out.println("Queue -> List");

        Queue<Person> employeeQueue = new LinkedList<>();

        employeeQueue.add(new Person("mahir", "Rahul", 22));
        employeeQueue.add(new Person("meera", "Priya", 21));
        employeeQueue.add(new Person("sita", "Arun", 20));

        System.out.println(employeeQueue);

        List<Person> employeeList = new ArrayList<>(employeeQueue);

        System.out.println(employeeList);
    }
}