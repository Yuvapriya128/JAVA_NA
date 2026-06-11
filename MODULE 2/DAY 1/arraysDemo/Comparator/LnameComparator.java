package arraysDemo.Comparator;

import java.util.Comparator;

public class LnameComparator implements Comparator<PersonComparator> {
    @Override
    public int compare(PersonComparator o1,PersonComparator o2){
        return o1.getLname().compareToIgnoreCase(o2.getLname());
    }
}
