package MyCollections2.Interconversion;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class setList {
    public static void main(String[] args) {
        List<Integer> list=new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(10);
        list.add(20);
        list.add(30);

        System.out.println(list);

        Set<Integer> intset=new HashSet<>(list);
        list=new ArrayList<>(intset);
        System.out.println(list);

        System.out.println(list instanceof List);
        System.out.println(list instanceof Set);

        System.out.println("String");
        List<String> nameList = new ArrayList<>();

        nameList.add("Aram");
        nameList.add("Sita");
        nameList.add("Ravana");
        nameList.add("Aram");
        nameList.add("Sita");
        nameList.add("Ravana");

        System.out.println(nameList);

        Set<String> uniqueNames = new HashSet<>(nameList);

        nameList = new ArrayList<>(uniqueNames);

        System.out.println(nameList);

    }
}
