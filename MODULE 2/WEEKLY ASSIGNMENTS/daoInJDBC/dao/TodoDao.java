package daoInJDBC.dao;

import daoInJDBC.entity.Todo;

import java.sql.SQLException;
import java.util.Collection;

public interface TodoDao {

    // INSERT
    int save(Todo todo) throws SQLException;

    // BASIC SELECT
    Collection<Todo> findAll() throws SQLException;

    // BOOLEAN CONDITION
    Collection<Todo> findCompletedTasks() throws SQLException ;

    // UPDATE
    void markAsCompleted(int id) throws SQLException ;

    // AGGREGATE
    int countTasks() throws SQLException ;

    // GROUP BY
    void groupByStatus() throws SQLException ;
}