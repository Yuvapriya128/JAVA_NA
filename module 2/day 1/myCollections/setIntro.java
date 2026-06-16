package MyCollections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import java.util.Set;

public class setIntro {
    public static void main(String[] args) {
        System.out.println("HashSet -- unordered");
        Set<Integer> intset =new HashSet<>();
        intset.add(100);
        intset.add(200);
        intset.add(100);
        intset.add(2);
        intset.add(3);
        intset.add(4);
        System.out.println(intset);
        intset.remove(3);
        System.out.println(intset);


        System.out.println("LinkedHashSet --ordered");
        Set<Integer> lintset=new LinkedHashSet<>();
        lintset.add(100);
        lintset.add(200);
        lintset.add(100);
        lintset.add(2);
        lintset.add(3);
        lintset.add(4);
        System.out.println(lintset);
        lintset.remove(100);
        System.out.println(lintset);


        System.out.println("TreeSet --sorted");
        Set<Integer> tintset=new TreeSet<>();
        tintset.add(100);
        tintset.add(200);
        tintset.add(100);
        tintset.add(2);
        tintset.add(3);
        tintset.add(4);
        System.out.println(tintset);
        tintset.remove(100);
        System.out.println(tintset);



    }
}
