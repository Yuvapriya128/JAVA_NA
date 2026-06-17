package main;

import config.TodoSpringConfig;
import dao.TodoDao;
import daoImpl.TodoDaoImpl;
import entity.Todo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;
import java.util.Scanner;

public class TodoMainCollections {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) throws SQLException {
        System.out.println("Todo With Spring Config , Collections");

        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        TodoDao todoDao=context.getBean("todo",TodoDao.class);


        System.out.println("1.save ");
        System.out.println("2.findById ");
        System.out.println("3.findAll ");
        System.out.println("4.deleteById ");
        System.out.println("5.updateById ");

        do {
            System.out.println("Enter option");
            int op= sc.nextInt();

            switch (op){
                case 1-> {
                    System.out.println("Enter id");
                    int id=sc.nextInt();

                    System.out.println("Enter task");
                    sc.nextLine();
                    String task=sc.nextLine();
                    System.out.println("Enter isdone(boolean)");
                    Boolean isdone=sc.nextBoolean();
                    todoDao.save(new Todo(id,task,isdone));
                }
                case 2->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    System.out.println(todoDao.findById(id));

                }
                case 3->{
                    todoDao.findAll().forEach(System.out::println);
                }
                case 4->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    todoDao.deleteById(id);

                }
                case 5->{
                    System.out.println("Enter id");
                    int id=sc.nextInt();
                    System.out.println("Enter task");
                    String task=sc.nextLine();
                    System.out.println("Enter isdone(boolean)");
                    Boolean isdone=sc.nextBoolean();
                    todoDao.updateById(id,new Todo(id,task,isdone));
                }
                default -> {throw new IllegalArgumentException("Invalid option");}
            }

        }while(true);
    }
}
