package daoInJDBC.Main;

import daoInJDBC.dao.BookDao;
import daoInJDBC.entity.Book;
import daoInJDBC.ui.Bookimpl;

import java.util.Scanner;
/*//    how it is created from interface reference

//
    used for whole class scanner
 */
public class BookMain {

    private static Bookimpl bookDao=new Bookimpl();

    private static Scanner sc=new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("\n===== BOOK MENU =====");
        System.out.println("1. Add Book");
        System.out.println("2. Get Book by ID");
        System.out.println("3. Get All Books");
        System.out.println("4. Find by Title & Publisher");
        System.out.println("5. Find by Title");
        System.out.println("6. Group By Author");
        System.out.println("7. Sort By Title");
        System.out.println("8. Find by Author (Ascending)");
        System.out.println("9. Check if Book Exists (by ID)");
        System.out.println("10. Delete All Books");
        System.out.println("11. Delete Book by ID");
        System.out.println("0. Exit");

     do{
         System.out.println("Enter option");
        int option=sc.nextInt();
        sc.nextLine();
        switch (option){
            case 1:
                addBook();
                break;
            case 2:
                get();
                break;
            case 3:
                getAll();
                break;
            case 4:
                getByTitlePublisher();
                break;
            case 5:
                getByTitle();
                break;
            case 6:
                getGroupbyAuthor();
                break;
            case 7:
                getSortByTitle();
                break;
            case 8:
                getByAuthorAsc();
                break;
            case 9:
                found();
                break;
            case 10:
                delAll();
                break;
            case 11:
                delById();
                break;
            case 12:
                updateWithId();
                break;
            case 0:
                System.exit(0);
            default:
                System.out.println("Invalid");;

        }
     }while (true);

    }
    static void getGroupbyAuthor(){
        System.out.println("To get group by author:");
        System.out.println("Enter author:");
        String author=sc.nextLine();
        bookDao.groupByAuthor(author);
    }
    static void getByTitlePublisher(){
        System.out.println("To find by title and publisher:");
        System.out.println("Enter title:");
        String title=sc.nextLine();
        System.out.println("Enter publisher:");
        String pub=sc.nextLine();
        bookDao.findByTitleAndPublisher(title,pub).forEach(System.out::println);
    }
    static void updateWithId(){
        System.out.println("To update with id and title:");
        System.out.println("Enter id:");
        int id=sc.nextInt();
        sc.nextLine();
        System.out.println("Enter title:");
        String title=sc.nextLine();
        bookDao.updateById(id,title);
    }
    static void found(){
        System.out.println("Exists or not:");
        int id=sc.nextInt();
        bookDao.existsById(id);

    }
    static void delById(){
        System.out.println("To delete by id(put id):");
        int id=sc.nextInt();
        bookDao.deleteById(id);
    }
    static void delAll(){
        System.out.println("Deleting all:");
        bookDao.deleteAll();
    }

    static void getByTitle(){
        System.out.println("To find by title:");
        System.out.println("Enter title:");
        String title=sc.nextLine();
        bookDao.findByTitle(title).forEach(System.out::println);
    }
    static void getByAuthorAsc(){
        System.out.println("To find by author ascending:");
        bookDao.findSortedByAuthorAsc();
    }
    static void getSortByTitle(){
        System.out.println("To find sorted title");
        System.out.println("Enter title:");
        bookDao.sortByTitle().forEach(System.out::println);
    }
    static void addBook(){

        System.out.println("To create book");
        System.out.print("Enter title: ");
        String title=sc.nextLine();

        System.out.print("Enter author: ");
        String author=sc.nextLine();

        System.out.print("Enter publisher: ");
        String publisher=sc.nextLine();

        Book book=new Book(title,author,publisher);
        int rows=bookDao.save(book);
        System.out.println("Rows updated:"+rows);

    }
    static void get(){

        System.out.println("To find book by id:");
        int id=sc.nextInt();

        System.out.println(bookDao.findById(id));

    }
    static void getAll(){
        System.out.println("To find all books:");
        bookDao.findAll().forEach(System.out::println);
    }
    
}
