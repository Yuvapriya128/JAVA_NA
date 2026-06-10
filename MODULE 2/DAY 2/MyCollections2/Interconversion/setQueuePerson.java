package MyCollections2.Interconversion;

import java.util.*;

public class setQueuePerson {

    public static void main(String[] args) {

        // Set -> Queue
        Set<Person> personSet = new LinkedHashSet<>();

        personSet.add(new Person("mahir", "Rahul", 22));
        personSet.add(new Person("meera", "Priya", 21));
        personSet.add(new Person("sita", "Arun", 20));

        System.out.println("Set -> Queue");
        System.out.println(personSet);

        Queue<Person> personQueue = new LinkedList<>(personSet);

        System.out.println(personQueue);

        System.out.println("\n----------------------\n");

        // Queue -> Set
        System.out.println("Queue -> Set");

        Queue<Person> employeeQueue = new LinkedList<>();

        employeeQueue.add(new Person("mahir", "Rahul", 22));
        employeeQueue.add(new Person("meera", "Priya", 21));
        employeeQueue.add(new Person("sita", "Arun", 20));
        employeeQueue.add(new Person("sita", "Arun", 20)); // duplicate

        System.out.println(employeeQueue);

        // Removes duplicates if equals() and hashCode() are overridden
        Set<Person> employeeSet = new LinkedHashSet<>(employeeQueue);

        System.out.println(employeeSet);
    }
}