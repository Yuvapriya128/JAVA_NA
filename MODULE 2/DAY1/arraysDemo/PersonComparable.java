package arraysDemo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

//<> -> means generate
public class PersonComparable implements Comparable<PersonComparable>{
    private String fname;
    private String lname;
    private int age;

//    ascending
//    @Override
//    public int compareTo(Person other){
//        return this.age-other.age;
//    }

    //    descending
//    @Override
//    public int compareTo(Person other){
//        return other.age-this.age;
//    }
//    ascending
    @Override
    public int compareTo(PersonComparable other){
        return this.fname.compareToIgnoreCase(other.fname);
    }
//    descending
//    @Override
//    public int compareTo(Person other){
//        return other.fname.compareTo(this.fname);
//    }

    public PersonComparable(String fname, String lname, int age) {
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
        PersonComparable[] people= {
                new PersonComparable("yuva", "priya", 21),
                new PersonComparable("Gokul", "nathan", 19),
                new PersonComparable("Amudha", "S", 40)
        };

        //        print everything
        System.out.println(Arrays.toString(people));

//        Arrays.sort(people, Comparator.comparingInt(Person::getAge));
//        to reverse
//         Arrays.sort(people, Comparator.comparingInt(Person::getAge).reversed());

        Arrays.sort(people);

//        prints separately using toString()
        for(PersonComparable p:people)
        System.out.println(p);

        System.out.println("Enter 1(age),2(fname),3(lname):");
        Scanner sc=new Scanner(System.in);
        int option=sc.nextInt();
        switch (option){
            case 1:
                Arrays.sort(people,Comparator.comparingInt(PersonComparable::getAge));
                System.out.println(Arrays.toString(people));
                break;
            case 2:
                Arrays.sort(people,Comparator.comparing(PersonComparable::getFname,String.CASE_INSENSITIVE_ORDER));
                System.out.println(Arrays.toString(people));
                break;
            case 3:
                Arrays.sort(people,Comparator.comparing(PersonComparable::getLname));
                System.out.println(Arrays.toString(people));
                break;
        }





    }
}
