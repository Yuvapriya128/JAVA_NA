package com.northernArc.firstbootapp.dao;

import com.northernArc.firstbootapp.model.Todo;

import java.util.Collection;
import java.util.Map;

public interface TodoDao {
    void save(Todo todo);
    void deleteById(int id);
    Todo findById(int id);
    Map<Integer,Todo> findAll();
    void updateById(int id,Todo todo);
}
