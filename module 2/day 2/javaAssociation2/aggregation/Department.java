package JavaAssociation2.Aggregation;

import java.util.HashSet;
import java.util.Set;

public class Department {
    private String name;
    private String hqlocation;
    private Set<Employee> employeeSet;

    public Department(String name, String hqlocation) {
        this.name = name;
        this.hqlocation = hqlocation;
        this.employeeSet=new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHqlocation() {
        return hqlocation;
    }

    public void setHqlocation(String hqlocation) {
        this.hqlocation = hqlocation;
    }
    public void addEmployees(Employee e){
        this.employeeSet.add(e);
    }
    public void showEmployees(){
        System.out.println(this.employeeSet);
    }
    public void removeEmployee(String fname){
        Employee removeemp=null;

        for(Employee e:employeeSet){
            if(e.getFname().equalsIgnoreCase(fname)){
                removeemp=e;
            }
        }
        if(removeemp!=null){
            employeeSet.remove(removeemp);
        }
    }
}
