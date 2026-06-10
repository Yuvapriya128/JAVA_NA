package DirectAccessObject.ui;

import DirectAccessObject.dao.TodoDao;
import DirectAccessObject.entity.Todo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Todoimpl implements TodoDao {
    private Set<Todo> todoSet;
//    constructor needed


    public Todoimpl() {
        this.todoSet = new LinkedHashSet<>();
    }

    @Override
    public void save(Todo t) {
        todoSet.add(t);

    }

    @Override
    public Todo findById(int id) {
        for(Todo t:todoSet){
            if(t.getId()==id){
                return t;
            }
        }
        return null;
    }

    @Override
    public Iterable<Todo> findAll() {

        return todoSet;
    }

    @Override
    public void deleteAll() {
        todoSet.clear();

    }

    @Override
    public Iterable<Todo> update(Todo t) {
      Iterator<Todo> todoIterator=todoSet.iterator();
      while(todoIterator.hasNext()){
          Todo temp=todoIterator.next();
          if(temp.getId()==t.getId()){
//              for assigning use setters , then getters
              temp.setTask(t.getTask());
              temp.setIsfinish(t.isIsfinish());
          }
      }
      return todoSet;

    }

    @Override
    public void deleteById(int id) {
//        Todo temp=null;
//        for(Todo eachtodo:todoSet){
//            if(eachtodo.getId()==id){
//                temp=eachtodo;
//            }
//        }
//        todoSet.remove(temp);
        Iterator<Todo> todoIterator2=todoSet.iterator();
        while(todoIterator2.hasNext()){
            Todo tempit2=todoIterator2.next();
            if(tempit2.getId()==id){
                todoIterator2.remove();
            }
        }
    }
}
