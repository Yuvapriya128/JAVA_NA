package entity;

import org.springframework.beans.factory.annotation.Qualifier;

public class Todo {
    private int id;
    private String task;
    private Boolean isfinish;

    public Todo(){}

//    this is for jdbc writing
    public Todo(String task, Boolean isfinish) {
        this.task = task;
        this.isfinish = isfinish;
    }

//    jdbc reading
    public Todo(int id, String task, Boolean isfinish) {
        this.id = id;
        this.task = task;
        this.isfinish = isfinish;
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

    public Boolean getIsFinish() {
        return isfinish;
    }

    public void setIsdone(Boolean isfinish) {
        this.isfinish = isfinish;
    }

    @Override
    public String toString(){
        return (id+" "+task+" "+isfinish);
    }
}
