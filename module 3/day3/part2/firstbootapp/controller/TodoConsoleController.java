package com.northernArc.firstbootapp.controller;

import com.northernArc.firstbootapp.dao.TodoDao;
import com.northernArc.firstbootapp.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class TodoConsoleController {
    @Autowired
    public TodoDao todoDao;
    @Autowired
    public Scanner sc;

    public void showMenu(){
        System.out.println("1.add");
        System.out.println("2.Find by ID");
        System.out.println("3.Find All");
        System.out.println("4.update by No");
        System.out.println("5.delete by No");
        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectChoice(option);

        }while(true);
    }
    void redirectChoice(int n){
        switch (n){
            case 1->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                System.out.println("Enter task");
                sc.nextLine();
                String task=sc.nextLine();
                System.out.println("Enter done:(boolean)");
                Boolean done=sc.nextBoolean();
                todoDao.save(new Todo(id,task,done));
            }
            case 2->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                System.out.println(todoDao.findById(id));

            }
            case 3->{
                todoDao.findAll().forEach((k,v)-> System.out.println(k+" :"+v));

            }
            case 4->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                System.out.println("Enter task");
                sc.nextLine();
                String task=sc.nextLine();
                System.out.println("Enter done:(boolean)");
                Boolean done=sc.nextBoolean();
                todoDao.updateById(id,new Todo(id,task,done));
            }
            case 5->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                todoDao.deleteById(id);

            }
            default -> {
                System.out.println("Invalid choice");
            }
        }
    }

}
