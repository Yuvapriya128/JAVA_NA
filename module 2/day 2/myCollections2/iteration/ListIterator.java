package MyCollections2.Iteration;

import java.util.*;

public class ListIterator {
    public static void main(String[] args) {
        /*List<String> strlist=new ArrayList<>(); --> Itr
           List<String> strlist=new LinkedList<>(); --> ListItr
           Set<String> strset=new HashSet<>(); --> KeyIterator
           Set<String> strset=new LinkedHashSet<>();  --> LinkedKeyIterator
           Set<String> strset=new TreeSet<>();  --> KeyIterator
           Queue<String> strq=new PriorityQueue<>(); --> Itr
           Deque<String> strq=new ArrayDeque<>();  --> DeqIterator
           Deque<String> strq=new LinkedList<>(); --> ListItr

        * */

        System.out.println("List");
        List<String> strlist=new LinkedList<>();
        strlist.add("apple");
        strlist.add("almond");
        strlist.add("alph");
        strlist.add("apple");
        strlist.add("almond");

        for(String str:strlist){
            System.out.println(str);
        }

        System.out.println("----------");


        Iterator<String> itr=strlist.iterator();
        System.out.println(itr.getClass().getSimpleName());
        while(itr.hasNext()){
            System.out.println(itr.next());
        }

        System.out.println("\nSet");
        Set<String> strset=new TreeSet<>();
        strset.add("apple");
        strset.add("almond");
        strset.add("alph");
        strset.add("apple");
        System.out.println("-------");

        Iterator<String> itrset=strset.iterator();
        System.out.println(itrset.getClass().getSimpleName());
        while(itrset.hasNext()){
            System.out.println(itrset.next());
        }

        System.out.println("\nQueue");
        Deque<String> strq=new ArrayDeque<>();
        strq.add("apple");
        strq.add("almond");
        strq.add("alph");
        strq.add("apple");
        System.out.println("-------");

        Iterator<String> itrq=strq.iterator();
        System.out.println(itrq.getClass().getSimpleName());
        while(itrq.hasNext()){
            System.out.println(itrq.next());
        }



    }
}
