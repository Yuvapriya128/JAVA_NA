package arraysDemo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Child {
    private String fname;
    private String lname;
    private String dob;

    public Child(String fname, String lname, String dob) {
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    @Override
    public String toString(){
        return ("\n"+String.join("-",this.fname,this.lname,this.dob));
        //return ("\n"+this.fname+" "+this.lname+" "+this.dob);
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        Child[] children = {
                new Child("Arun", "Kumar", "12-05-2015"),
                new Child("Priya", "Shankar", "23-08-2014"),
                new Child("Kavin", "Raj", "15-01-2016"),
                new Child("Nila", "Devi", "30-11-2015")

        };
        System.out.println(Arrays.toString(children));
        System.out.println("----------------\nAfter sorting by dob");
        Arrays.sort(children,new Comparator<Child>(){
            public int compare(Child c1,Child c2){
                String[] c1dob=c1.getDob().split("-");
                String[] c2dob= c2.getDob().split("-");
                if(c1dob[2]==c2dob[2]){
                    if(c1dob[1]==c2dob[1]){
                        return Integer.parseInt(c1dob[0])-Integer.parseInt(c2dob[0]);
                    }else{
                        return Integer.parseInt(c1dob[1])-Integer.parseInt(c2dob[2]);
                    }
                }else{
                    return Integer.parseInt(c1dob[2])-Integer.parseInt(c2dob[2]);
                }
            }
        });

        System.out.println(Arrays.toString(children));
    }
}
