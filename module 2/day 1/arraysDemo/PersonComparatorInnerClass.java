package arraysDemo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class PersonComparatorInnerClass {
    private String fname;
    private String lname;
    private int age;

    public PersonComparatorInnerClass(String fname, String lname, int age) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public String toString(){
        return (this.fname+" "+this.lname+" "+this.age);
    }

    public static void main(String[] args) {
         class AgeComparator implements Comparator<PersonComparatorInnerClass> {
            @Override
            public int compare(PersonComparatorInnerClass p1,PersonComparatorInnerClass p2){
                return  p1.getAge()-p2.getAge();
            }
        }
         class FnameComparator implements Comparator<PersonComparatorInnerClass> {
            @Override
            public int compare(PersonComparatorInnerClass p1,PersonComparatorInnerClass p2){
                return p1.getFname().compareToIgnoreCase(p2.getFname());
            }
        }
        class LnameComparator implements Comparator<PersonComparatorInnerClass> {
            @Override
            public int compare(PersonComparatorInnerClass o1,PersonComparatorInnerClass o2){
                return o1.getLname().compareToIgnoreCase(o2.getLname());
            }
        }

        PersonComparatorInnerClass[] people={
                new PersonComparatorInnerClass("abdul","kalam",68),
                new PersonComparatorInnerClass("rahul","gandhi",76),
                new PersonComparatorInnerClass("jaya","lalitha",59),
                new PersonComparatorInnerClass("marie","curie",50)

        };
        System.out.println(Arrays.toString(people));
        Scanner sc=new Scanner(System.in);

        System.out.println("Enter option:(1.age,2.fname,3.lname):");
        int n=sc.nextInt();
        switch (n){
            case 1:
                Arrays.sort(people,new AgeComparator());
                System.out.println(Arrays.toString(people));
                break;
            case 2:
                Arrays.sort(people,new FnameComparator());
                System.out.println(Arrays.toString(people));
                break;
            case 3:
                Arrays.sort(people,new LnameComparator());
                System.out.println(Arrays.toString(people));
                break;
        }


    }
}

