package controller;

import dao.TodoDao;
import entity.Todo;

import java.sql.SQLException;
import java.util.Scanner;

public class TodoConsoleController {
    private Scanner sc;
    private TodoDao todoDao;

    public TodoConsoleController(Scanner sc, TodoDao todoDao) {
        this.sc = sc;
        this.todoDao = todoDao;
    }
    public void welcome() {
        System.out.println("Welcome to Todo controller");
    }

    public void showMenu() throws SQLException {
        System.out.println("1.save ");
        System.out.println("2.findById ");
        System.out.println("3.findAll ");
        System.out.println("4.deleteById ");
        System.out.println("5.updateById ");

        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectMenu(option);
        }while(true);
    }

    private void redirectMenu(int option) throws SQLException {
        switch (option){
            case 1->{add();}
            case 2->{getById();}
            case 3->{getAll();}
            case 4->{deleteById();}
            case 5-> {updateById();}
            default -> {throw new IllegalArgumentException("Invalid choide");}
        }

    }


    private void add() throws SQLException {
        System.out.println("Enter id");
        int id=sc.nextInt();

        System.out.println("Enter task");
        sc.nextLine();
        String task=sc.nextLine();
        System.out.println("Enter isdone(boolean)");
        Boolean isdone=sc.nextBoolean();
        todoDao.save(new Todo(id,task,isdone));
    }
    private void getById() throws SQLException{
        System.out.println("Enter id");
        int id=sc.nextInt();
        System.out.println(todoDao.findById(id));
    }
    private void getAll(){
        try {
            todoDao.findAll().forEach(System.out::println);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }catch(NullPointerException e){
            System.out.println("No elements present"+e.getMessage());
        }
    }
    private void deleteById() throws SQLException {
        System.out.println("Enter id");
        int id=sc.nextInt();
        todoDao.deleteById(id);
    }
    private void updateById() throws SQLException{
        System.out.println("Enter id");
        int id=sc.nextInt();
        System.out.println("Enter task");
        String task=sc.nextLine();
        System.out.println("Enter isdone(boolean)");
        Boolean isdone=sc.nextBoolean();
        todoDao.updateById(id,new Todo(id,task,isdone));
    }
}
