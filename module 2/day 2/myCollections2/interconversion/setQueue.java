package MyCollections2.Interconversion;

import java.util.*;

public class setQueue {
    public static void main(String[] args) {
        Queue<Integer> qu=new PriorityQueue<>();
        qu.add(10);
        qu.add(20);
        qu.add(30);
        qu.add(10);
        qu.add(20);
        qu.add(30);
        System.out.println(qu);
        Set<Integer> intset=new LinkedHashSet<>(qu);
        qu=new PriorityQueue<>(intset);

        System.out.println(qu);

        System.out.println(qu instanceof Queue);
        System.out.println(qu instanceof Set);

        System.out.println("String");
        Queue<String> nameQueue = new PriorityQueue<>();

        nameQueue.add("Aram");
        nameQueue.add("Sita");
        nameQueue.add("Ravana");
        nameQueue.add("Aram");
        nameQueue.add("Sita");
        nameQueue.add("Ravana");

        System.out.println(nameQueue);

        Set<String> uniqueNameSet = new LinkedHashSet<>(nameQueue);

        nameQueue = new PriorityQueue<>(uniqueNameSet);

        System.out.println(nameQueue);


    }
}
