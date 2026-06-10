package DirectAccessObject;

import DirectAccessObject.entity.Book;
import DirectAccessObject.ui.Bookimpl;

import java.util.Scanner;

public class MainBook {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Bookimpl bookdao = new Bookimpl();

        System.out.println("========== BOOK MENU ==========");
        System.out.println("\n1 -> Save a Book");
        System.out.println("2 -> Find Book by ID");
        System.out.println("3 -> Delete Book by ID");
        System.out.println("4 -> Update Book Details");
        System.out.println("5 -> Delete All Books");
        System.out.println("6 -> Display All Books");
        System.out.println("7 -> Search Books by Author");
        System.out.println("8 -> Sort Books by Title (Ascending)");
        System.out.println("9 -> Sort Books by Title (Descending)");
        System.out.println("10 -> Exit");

        do {
            System.out.print("\nEnter option : ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {

                case 1:

                    System.out.println("Enter ISBN : ");
                    int isbn = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Enter Title : ");
                    String title = sc.nextLine();

                    System.out.println("Enter Author : ");
                    String author = sc.nextLine();

                    Book b1 = new Book(isbn, title, author);

                    bookdao.save(b1);

                    System.out.println("Book Saved Successfully");

                    break;

                case 2:

                    System.out.println("Enter ISBN to find : ");
                    int findId = sc.nextInt();

                    Book foundBook = bookdao.findById(findId);

                    if (foundBook != null) {
                        System.out.println(foundBook);
                    }
                    else {
                        System.out.println("Book Not Found");
                    }

                    break;

                case 3:

                    System.out.println("Enter ISBN to delete : ");
                    int deleteId = sc.nextInt();

                    bookdao.deleteById(deleteId);

                    System.out.println("Book Deleted Successfully");

                    break;

                case 4:

                    System.out.println("Enter ISBN to update : ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Enter New Title : ");
                    String newTitle = sc.nextLine();

                    System.out.println("Enter New Author : ");
                    String newAuthor = sc.nextLine();

                    Book updatedBook =
                            new Book(updateId, newTitle, newAuthor);

                    bookdao.update(updatedBook);

                    System.out.println("Book Updated Successfully");

                    break;

                case 5:

                    bookdao.deleteAll();

                    System.out.println("All Books Deleted");

                    break;

                case 6:

                    Iterable<Book> allBooks = bookdao.findAll();

                    for (Book b : allBooks) {
                        System.out.println(b);
                    }

                    break;

                case 7:

                    System.out.println("Enter Author Name : ");

                    String searchAuthor = sc.nextLine();

                    Iterable<Book> authorBooks =
                            bookdao.findByAuthor(searchAuthor);

                    for (Book b : authorBooks) {
                        System.out.println(b);
                    }

                    break;

                case 8:

                    Iterable<Book> ascBooks =
                            bookdao.sortByTitleAsc();

                    for (Book b : ascBooks) {
                        System.out.println(b);
                    }

                    break;

                case 9:

                    Iterable<Book> descBooks =
                            bookdao.sortByTitleDesc();

                    for (Book b : descBooks) {
                        System.out.println(b);
                    }

                    break;

                case 10:

                    System.out.println("Exiting...");
                    System.exit(0);

                default:

                    System.out.println("Invalid Option");
            }
        }
        while (true);
    }
}