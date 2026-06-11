package MyCollections;

import java.util.LinkedList;
import java.util.Queue;

import arraysDemo.Comparator.AgeComparator;
import arraysDemo.Comparator.FnameComparator;
import arraysDemo.Comparator.LnameComparator;
import arraysDemo.Comparator.PersonComparator;

public class QueueLinkedlist {
    public static void main(String[] args) {
        Queue<Integer> q=new LinkedList<>(); //upcasting linkedlist -> dequeue -> queue
//        offer == add
//        poll == remove
        q.add(1);
        q.add(2);
        q.add(3);
        System.out.println(q);
        q.remove(1);// queue removes object
        System.out.println(q);

//        string
        Queue<String> stringQueue=new LinkedList<>();
        stringQueue.add("apple");
        stringQueue.add("zee");
        stringQueue.add("yuva");
        System.out.println(stringQueue);
        stringQueue.remove("zee");
        System.out.println(stringQueue);

//        Person
        Queue<PersonComparator> queueperson=new LinkedList<>();
        queueperson.add(new PersonComparator("siva","nantham",51));
        queueperson.add(new PersonComparator("amudha","S",41));
        System.out.println(queueperson);
        queueperson.remove(); //fifo
        System.out.println(queueperson);


    }
}
