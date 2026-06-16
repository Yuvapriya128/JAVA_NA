package daoInMap.Main;

import daoInMap.Dao.PersonDao;
import daoInMap.Entity.Person;
import daoInMap.UI.PersonImpl;

import java.util.Scanner;

public class PersonMain {
    public static PersonDao personDao=new PersonImpl();
    public static Scanner sc=new Scanner(System.in);

    public static void main(String[] args) {
/*   public void save(Person p);
    public Collection<Person> findAll();
    public Person findById(int id);
    public void deletedById(int id);
    public void updateById(int id,Person p);*/
        System.out.println("Person menu");
        System.out.println("1.add person");
        System.out.println("2.find all ");
        System.out.println("3.find by id");
        System.out.println("4.delete by id");
        System.out.println("5.update by id");
        System.out.println("0.exit");

        do {
            System.out.println("Enter option:");
            int option = sc.nextInt();

            switch (option){
                case 1->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter fname");
                    String fname=sc.nextLine();
                    System.out.println("Enter lname");
                    String lname=sc.nextLine();

                    System.out.println("Enter age");
                    int age=sc.nextInt();
                    personDao.save(new Person(id,fname,lname,age));
                }
                case 2->{
                    personDao.findAll().forEach(System.out::println);
                }
                case 3->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    System.out.println( personDao.findById(id));
                }
                case 4->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    personDao.deletedById(id);
                }
                case 5->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter fname");
                    String fname=sc.nextLine();
                    System.out.println("Enter lname");
                    String lname=sc.nextLine();

                    System.out.println("Enter age");
                    int age=sc.nextInt();
                    personDao.updateById(id,new Person(id,fname,lname,age));
                }

                case 0 -> System.exit(0);
                default -> System.out.println("invalid option");
            }
        }while (true);

    }
}
