package com.northernArc.firstbootapp.dao;

import com.northernArc.firstbootapp.model.Todo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TodoDaoImplementor implements TodoDao{
    /*Implementors :
    * make it component
    * override functions
    * Do postConstructor: for dummy values to come
    * Do preDestroy: for clearing collection or closing db/files
    * make todoConsoleController -> service
    *     There do @Autowired for Scanner and TodoDao
    * Create @Bean for Scanner in @Configuration
    * In Main App: do @Autowired for ConsoleController
    *      implements CommandLineRunner
    *      and override public void run() throws Exception
    * */
    Map<Integer,Todo> todoMap=new HashMap<>();
    @Override
    public void save(Todo todo) {
       todoMap.put(todo.getId(), todo);
    }

    @Override
    public void deleteById(int id) {
        todoMap.remove(todoMap.get(id));

    }

    @Override
    public Todo findById(int id) {
        return todoMap.get(id);
    }

    @Override
    public Map<Integer,Todo> findAll() {
        return todoMap;
    }

    @Override
    public void updateById(int id, Todo todo) {
        todoMap.put(todo.getId(), todo);

    }

    @PreDestroy
    public void close(){
        System.out.println("Clearing Map");
        todoMap.clear();
    }

    @PostConstruct
    public void init(){
        System.out.println("");
        todoMap.put(1,new Todo(1,"wake up",true));
        todoMap.put(2,new Todo(2,"drink water",true));
        todoMap.put(3,new Todo("pray",false));
    }
}
