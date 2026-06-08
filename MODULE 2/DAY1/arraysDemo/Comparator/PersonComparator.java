package arraysDemo.Comparator;

import java.util.Arrays;
import java.util.Scanner;

public class PersonComparator {
    private String fname;
    private String lname;
    private int age;

    public PersonComparator(String fname, String lname, int age) {
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
        PersonComparator[] people={
                new PersonComparator("abdul","kalam",68),
                new PersonComparator("rahul","gandhi",76),
                new PersonComparator("jaya","lalitha",59),
                new PersonComparator("marie","curie",50)

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
