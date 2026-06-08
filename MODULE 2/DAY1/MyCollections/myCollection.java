package MyCollections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class myCollection  {
//    public int size(){
//
//    }
//    public boolean isEmpty(){
//
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return false;
//    }
//
//    @Override
//    public Iterator iterator() {
//        return null;
//    }
//
//    @Override
//    public void forEach(Consumer action) {
//        Collection.super.forEach(action);
//    }
//
//    @Override
//    public Object[] toArray() {
//        return new Object[0];
//    }
//
//    @Override
//    public Object[] toArray(IntFunction generator) {
//        return Collection.super.toArray(generator);
//    }
//
//    @Override
//    public boolean add(Object o) {
//        return false;
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return false;
//    }
//
//    @Override
//    public boolean addAll(Collection c) {
//        return false;
//    }
//
//    @Override
//    public boolean removeIf(Predicate filter) {
//        return Collection.super.removeIf(filter);
//    }
//
//    @Override
//    public void clear() {
//
//    }
//
//    @Override
//    public Spliterator spliterator() {
//        return Collection.super.spliterator();
//    }
//
//    @Override
//    public Stream stream() {
//        return Collection.super.stream();
//    }
//
//    @Override
//    public Stream parallelStream() {
//        return Collection.super.parallelStream();
//    }
//
//    @Override
//    public boolean retainAll(Collection c) {
//        return false;
//    }
//
//    @Override
//    public boolean removeAll(Collection c) {
//        return false;
//    }
//
//    @Override
//    public boolean containsAll(Collection c) {
//        return false;
//    }
//
//    @Override
//    public Object[] toArray(Object[] a) {
//        return new Object[0];
//    }

    public static void main(String[] args) {
//        get -> read
//        set -> set index 0 with value in index 1
        List<Integer> list=new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        System.out.println(list);
        list.remove(1);// this is index
        list.set(0,8); //at index 0 set 8
        System.out.println(list.get(1));
        boolean iscontain=list.contains(1);
        System.out.println(list);

//        Sorting and Reversing
        Collections.sort(list);
        System.out.println(list);
        Collections.sort(list.reversed());
        System.out.println(list);

//        Double list
        List<Double> doublelist=new ArrayList<>();
        doublelist.add(3.5);
        doublelist.add(1.3);
        System.out.println(doublelist);
        System.out.println(doublelist.get(0));
        System.out.println(doublelist.size());
        doublelist.set(0,4.3);
        System.out.println(doublelist);

//        String list
        List<String> stringlist=new ArrayList<>();
        stringlist.add("Apple");
        stringlist.add("orange");
        stringlist.add("papaya");
        System.out.println(stringlist);
        stringlist.set(0,"mango");
        Collections.sort(stringlist);
        System.out.println(stringlist);
        stringlist.remove("orange");
        System.out.println(stringlist);

//        Person list
        List<Person> people=new ArrayList<>();
        people.add(new Person("yuva","priya",21));
        people.add(new Person("shanmu","harini",22));
        people.addFirst(new Person("ammu","S",41));
        System.out.println(people.toString());
        people.remove(0);
        System.out.println(people.toString());
        people.set(0,new Person("siva","nantham",51));
        System.out.println(people.toString());

//        Linked list
        List<Integer> llist=new LinkedList<>();
        llist.add(1);
        llist.add(2);
        llist.add(5);
        llist.add(6);
        System.out.println(llist);
        llist.remove(0);
        System.out.println(llist);
        llist.remove("2"); //not working -> no error
        System.out.println(llist);





    }
}
