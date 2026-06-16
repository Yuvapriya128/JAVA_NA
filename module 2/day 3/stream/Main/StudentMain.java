package stream.Main;

import stream.connectorui.Studentimpl;
import stream.entity.Student;

import java.util.Collection;

public class StudentMain {

    public static void main(String[] args) {

        Studentimpl studentimpl = new Studentimpl();

        studentimpl.add(new Student("Yuva", 95, 88, 92, 85, 90));
        studentimpl.add(new Student("Arun", 78, 91, 89, 80, 87));
        studentimpl.add(new Student("Meera", 99, 96, 94, 92, 98));
        studentimpl.add(new Student("Rahul", 65, 72, 70, 68, 75));
        studentimpl.add(new Student("Priya", 88, 85, 90, 95, 91));

        System.out.println("===== ALL STUDENTS =====");

//        for (Student s : studentimpl.findAll()) {
//            System.out.println(s);
//        }

        studentimpl.findAll().forEach(System.out::println);


        System.out.println("\n===== MAX MARK PER SUBJECT =====");
        studentimpl.maxPerSub();

        System.out.println("\n===== AVERAGE MARK PER SUBJECT =====");
        studentimpl.avgPerSub();

        System.out.println("\n===== TOPPER PER SUBJECT =====");

//        for (Student s : studentimpl.topperPerSub()) {
//            System.out.println(s);
//        }

        studentimpl.topperPerSub().forEach(System.out::println);

        System.out.println("\n===== TOTAL TOPPER =====");
        studentimpl.topperTotal();

        System.out.println("\n===== ABOVE AVERAGE PHYSICS =====");
// This works for iterable : only enhanced for loop
//        for (Student s : studentimpl.aboveAvgPhy()) {
//            System.out.println(s);
//        }

//       This works For collection
        studentimpl.aboveAvgPhy().forEach(System.out::println);

    }
}