package DirectAccessObject.dao;

import DirectAccessObject.entity.Todo;

import java.util.Iterator;

public interface TodoDao {
    public void save(Todo t);
    public Todo findById(int id);
    public Iterable<Todo> findAll();
    public void deleteAll();
    public Iterable<Todo> update(Todo t);
    public void deleteById(int id);

}
