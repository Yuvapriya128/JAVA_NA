package daoImpl;

import dao.TodoDao;
import entity.Todo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TodoDaoImpl implements TodoDao {
    Map<Integer,Todo> todoMap=new LinkedHashMap<>();
    @Override
    public void save(Todo todo) {
        todoMap.put(todo.getId(),todo);
    }

    @Override
    public Todo findById(int id) {
        return todoMap.get(id);

    }

    @Override
    public Collection<Todo> findAll() {
        return todoMap.values();
    }

    @Override
    public void deleteById(int id) {
        todoMap.remove(id);

    }

    @Override
    public void updateById(int id, Todo todo) {
        todoMap.put(id,todo);
        System.out.println("Updated");
    }
}
