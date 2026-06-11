package MyCollections;

import java.util.HashSet;
import java.util.Set;

public class SetPerson {
    public static void main(String[] args) {
        Set<Person> personSet=new HashSet<>();
        personSet.add(new Person("yuva","priya",21));
        personSet.add(new Person("yuva","priya",21));
        personSet.add(new Person("yuva","priya",21));
        System.out.println(personSet);

//        before hashcode and equal it gives 3
//        after that it considers it as same

    }
}
