package DirectAccessObject;

import DirectAccessObject.entity.Todo;
import DirectAccessObject.ui.Todoimpl;

import java.util.Scanner;

public class MainTodo {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Todoimpl todoDao = new Todoimpl();

        System.out.println("========== TODO MENU ==========");
        System.out.println("1 -> Save Todo");
        System.out.println("2 -> Find Todo By ID");
        System.out.println("3 -> Delete Todo By ID");
        System.out.println("4 -> Update Todo");
        System.out.println("5 -> Delete All Todos");
        System.out.println("6 -> Display All Todos");
        System.out.println("7 -> Exit");

        do {

            System.out.print("\nEnter option : ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {

                case 1:

                    System.out.print("Enter Todo ID : ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Task : ");
                    String task = sc.nextLine();

                    System.out.print("Is Finished (true/false) : ");
                    boolean isFinish = sc.nextBoolean();

                    Todo todo = new Todo(id, task, isFinish);

                    todoDao.save(todo);

                    System.out.println("Todo Saved Successfully");

                    break;

                case 2:

                    System.out.print("Enter Todo ID : ");
                    int findId = sc.nextInt();

                    Todo foundTodo = todoDao.findById(findId);

                    if (foundTodo != null) {
                        System.out.println(foundTodo);
                    } else {
                        System.out.println("Todo Not Found");
                    }

                    break;

                case 3:

                    System.out.print("Enter Todo ID to delete : ");
                    int deleteId = sc.nextInt();

                    todoDao.deleteById(deleteId);

                    System.out.println("Todo Deleted Successfully");

                    break;

                case 4:

                    System.out.print("Enter Todo ID to update : ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter New Task : ");
                    String updateTask = sc.nextLine();

                    System.out.print("Is Finished (true/false) : ");
                    boolean updateStatus = sc.nextBoolean();

                    Todo updatedTodo =
                            new Todo(updateId, updateTask, updateStatus);

                    todoDao.update(updatedTodo);

                    System.out.println("Todo Updated Successfully");

                    break;

                case 5:

                    todoDao.deleteAll();

                    System.out.println("All Todos Deleted");

                    break;

                case 6:

                    Iterable<Todo> allTodos = todoDao.findAll();

                    for (Todo t : allTodos) {
                        System.out.println(t);
                    }

                    break;

                case 7:

                    System.out.println("Exiting...");
                    System.exit(0);

                default:

                    System.out.println("Invalid Option");
            }

        } while (true);
    }
}