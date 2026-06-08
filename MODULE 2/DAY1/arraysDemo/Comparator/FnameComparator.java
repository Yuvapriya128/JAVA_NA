package arraysDemo.Comparator;

import java.util.Comparator;

public class FnameComparator implements Comparator<PersonComparator> {
    @Override
    public int compare(PersonComparator p1,PersonComparator p2){
        return p1.getFname().compareToIgnoreCase(p2.getFname());
    }
}
