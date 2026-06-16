package MyCollections2.Interconversion;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class listQueue {
    public static void main(String[] args) {
//        list -> queue when fifo needed
        List<Integer> list=new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(10);
        list.add(20);
        list.add(30);

        System.out.println(list);

        Queue<Integer> qtemp=new PriorityQueue<>(list);
        list=new ArrayList<>(qtemp);

        System.out.println(list instanceof Queue);
        System.out.println(list instanceof List);


        System.out.println("String");
        List<String> strlist=new ArrayList<>();
        strlist.add("apple");
        strlist.add("almond");
        strlist.add("alph");
        strlist.add("apple");
        strlist.add("almond");
        strlist.add("alph");

        System.out.println(strlist);

        Queue<String> strqtemp=new PriorityQueue<>(strlist);
        strlist=new ArrayList<>(strqtemp);

        System.out.println(strlist);



    }
}
