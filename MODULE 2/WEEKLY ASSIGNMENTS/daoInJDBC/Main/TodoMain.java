package daoInJDBC.Main;

import daoInJDBC.dao.TodoDao;
import daoInJDBC.entity.Todo;
import daoInJDBC.ui.TodoImpl;

import java.sql.SQLException;
import java.util.Scanner;
/*    // INSERT
    int save(Todo todo) throws SQLException;
    Collection<Todo> findAll() throws SQLException;
    Collection<Todo> findCompletedTasks() throws SQLException ;
    void markAsCompleted(int id) throws SQLException ;
    int countTasks() throws SQLException ;
    void groupByStatus() throws SQLException ;*/

public class TodoMain {
    public static Scanner sc=new Scanner(System.in);
    private static TodoDao todoDao=new TodoImpl();

    public static void main(String[] args) throws SQLException {
        System.out.println("Todo menu:");
        System.out.println("1.add todo");
        System.out.println("2.findAll todo");
        System.out.println("3.findCompleted todo");
        System.out.println("4.markasCompleted todo");
        System.out.println("5.count todo");
        System.out.println("6.groupbyStatus todo");
        System.out.println("0.exit");

        do {
            System.out.println("Enter option:");
            int option= sc.nextInt();
            switch (option){
                case 1 -> add();
                case 2 -> todoDao.findAll().forEach(System.out::println);
                case 3 -> todoDao.findCompletedTasks().forEach(System.out::println);
                case 4 -> markcom();
                case 5 -> System.out.println(todoDao.countTasks());
                case 6 -> todoDao.groupByStatus();
                case 0 -> System.exit(0);
                default->
                    System.out.println("invalid option");
            }

        }while(true);
    }
    static void add() throws SQLException {
        sc.nextLine();
        System.out.println("Enter task");
        String task=sc.nextLine();

        System.out.println("Enter isFinished:(boolean)");
        Boolean isfinish=sc.nextBoolean();
        Todo todo=new Todo(task,isfinish);
        todoDao.save(todo);
    }
    static void markcom() throws SQLException{
        System.out.println("Enter id to set task marked:");
        int id=sc.nextInt();
        todoDao.markAsCompleted(id);
    }

}
