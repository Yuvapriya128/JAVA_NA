package MyCollections2;

import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class Dequeue {
    public static void main(String[] args) {

//        Deque<Integer> deq=new ArrayDeque<>();

        Deque<Integer> deqll=new LinkedList<>();
        deqll.add(1);
        deqll.add(3);
        deqll.add(2);
        System.out.println(deqll);
        deqll.remove(2);
        System.out.println(deqll);

        Deque<String> deqstr=new LinkedList<>();
        deqstr.add("aram");
        deqstr.add("sita");
        deqstr.add("aaa");
        deqstr.add("ravana");
        System.out.println(deqstr);
        deqstr.remove("sita");
        System.out.println(deqstr);

        Deque<Person> deqperson=new LinkedList<>();
        deqperson.add(new Person("yuva","priya",21));
        deqperson.add(new Person("siva","nantham",51));
        deqperson.add(new Person("gokul","nathan",19));
        deqperson.add(new Person("amudha","S",41));
        System.out.println(deqperson);
        deqperson.remove();
        System.out.println(deqperson);
//        deqperson.sort(new Comparator<Person>(){
//                @Override
//                public int compare(Person p1,Person p2){
//                    return p1.age-p2.age;
//        }
//        );

    }
}
