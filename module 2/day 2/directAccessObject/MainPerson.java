package DirectAccessObject;

import DirectAccessObject.entity.Person;
import DirectAccessObject.ui.Personimpl;

import java.util.Scanner;

public class MainPerson {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Personimpl persondao = new Personimpl();

        System.out.println("========== PERSON MENU ==========");
        System.out.println("1 -> Save a Person");
        System.out.println("2 -> Find Person by First Name");
        System.out.println("3 -> Delete Person by First Name");
        System.out.println("4 -> Update Person Details");
        System.out.println("5 -> Delete All Persons");
        System.out.println("6 -> Display All Persons");
        System.out.println("7 -> Search Persons by Last Name");
        System.out.println("8 -> Sort Persons by First Name (Ascending)");
        System.out.println("9 -> Sort Persons by Age (Descending)");
        System.out.println("10 -> Exit");

        do {

            System.out.print("\nEnter option : ");

            int option = sc.nextInt();
            sc.nextLine();

            switch(option) {

                case 1:

                    System.out.println("Enter First Name : ");
                    String fname = sc.nextLine();

                    System.out.println("Enter Last Name : ");
                    String lname = sc.nextLine();

                    System.out.println("Enter Age : ");
                    int age = sc.nextInt();

                    Person p1 = new Person(fname, lname, age);

                    persondao.save(p1);

                    System.out.println("Person Saved Successfully");

                    break;

                case 2:

                    System.out.println("Enter First Name to find : ");
                    String findName = sc.nextLine();

                    Person foundPerson =
                            persondao.findByFname(findName);

                    if(foundPerson != null) {

                        System.out.println(foundPerson);
                    }
                    else {

                        System.out.println("Person Not Found");
                    }

                    break;

                case 3:

                    System.out.println("Enter First Name to delete : ");
                    String deleteName = sc.nextLine();

                    persondao.deleteByFname(deleteName);

                    System.out.println("Person Deleted Successfully");

                    break;

                case 4:

                    System.out.println("Enter First Name to update : ");
                    String updateName = sc.nextLine();

                    System.out.println("Enter New Last Name : ");
                    String newLname = sc.nextLine();

                    System.out.println("Enter New Age : ");
                    int newAge = sc.nextInt();

                    Person updatePerson =
                            new Person(updateName, newLname, newAge);

                    persondao.update(updatePerson);

                    System.out.println("Person Updated Successfully");

                    break;

                case 5:

                    persondao.deleteAll();

                    System.out.println("All Persons Deleted");

                    break;

                case 6:

                    Iterable<Person> allPersons =
                            persondao.findAll();

                    for(Person p : allPersons) {

                        System.out.println(p);
                    }

                    break;

                case 7:

                    System.out.println("Enter Last Name : ");

                    String searchLname = sc.nextLine();

                    Iterable<Person> lnamePersons =
                            persondao.findByLname(searchLname);

                    for(Person p : lnamePersons) {

                        System.out.println(p);
                    }

                    break;

                case 8:

                    Iterable<Person> ascPersons =
                            persondao.sortByFnameAsc();

                    for(Person p : ascPersons) {

                        System.out.println(p);
                    }

                    break;

                case 9:

                    Iterable<Person> agePersons =
                            persondao.sortByAgeDesc();

                    for(Person p : agePersons) {

                        System.out.println(p);
                    }

                    break;

                case 10:

                    System.out.println("Exiting...");
                    System.exit(0);

                default:

                    System.out.println("Invalid Option");
            }

        } while(true);
    }
}