package dao;

import entity.Todo;

import java.sql.SQLException;
import java.util.Collection;

public interface TodoDao {
    void save(Todo todo) throws SQLException;
    Todo findById(int id) throws SQLException;
    Collection<Todo> findAll() throws SQLException;
    void deleteById(int id) throws SQLException;
    void updateById(int id,Todo todo) throws SQLException;
}
