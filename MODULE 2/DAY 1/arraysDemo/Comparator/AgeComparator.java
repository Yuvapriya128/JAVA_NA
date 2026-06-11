package arraysDemo.Comparator;

import java.util.Comparator;

public class AgeComparator implements Comparator<PersonComparator> {
    @Override
    public int compare(PersonComparator p1,PersonComparator p2){
        return  p1.getAge()-p2.getAge();
    }
}
