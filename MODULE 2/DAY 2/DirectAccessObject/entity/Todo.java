package DirectAccessObject.entity;

public class Todo {
    private int id;
    private String task;
    private boolean isfinish;

    public Todo(int id, String task, boolean isfinish) {
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

    public boolean isIsfinish() {
        return isfinish;
    }

    public void setIsfinish(boolean isfinish) {
        this.isfinish = isfinish;
    }

    @Override
    public String toString(){
        return (this.id+" "+this.task+" "+this.isfinish);
    }
}
