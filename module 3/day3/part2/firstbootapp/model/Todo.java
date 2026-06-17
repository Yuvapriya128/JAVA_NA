package com.northernArc.firstbootapp.model;

public class Todo {
    private int id;
    private String task;
    private boolean done;
// 3 constructors and toString
    @Override
    public String toString(){
        return (id+" "+task+" "+done);
    }
    public Todo() {
    }

    public Todo(String task, boolean done) {
        this.task = task;
        this.done = done;
    }

    public Todo(int id, String task, boolean done) {
        this.id = id;
        this.task = task;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
