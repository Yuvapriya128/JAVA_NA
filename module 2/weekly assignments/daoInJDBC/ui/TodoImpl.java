package daoInJDBC.ui;

import daoInJDBC.Connection.DBManager;
import daoInJDBC.dao.TodoDao;
import daoInJDBC.entity.Todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * to create table ->use postgresql in cmd
 * add throws SQLException to all methods
 * else have to use try/catch for using Connection
 *
 *   private int id;
    private String task;
    private boolean isfinish;
 * */
public class TodoImpl implements TodoDao {
    public Todo mapToTodo(ResultSet rs) throws SQLException {
        return new Todo(rs.getInt(1),rs.getString(2),rs.getBoolean(3));
    }
    @Override
    public int save(Todo todo) throws SQLException {
        Connection con= DBManager.getConnection();
        int row=0;
        String sql="Insert into todo(task,isfinish) values(?,?)";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,todo.getTask());
        stmt.setBoolean(2,todo.isIsfinish());
        row=stmt.executeUpdate();
        DBManager.closeConnection(con);
        return row;
    }

    @Override
    public Collection<Todo> findAll() throws SQLException {
        Connection con= DBManager.getConnection();
        List<Todo> todoList=new LinkedList<>();

        String sql="select * from todo";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            todoList.add(mapToTodo(rs));
        }
        DBManager.closeConnection(con);
        return todoList;
    }

    @Override
    public Collection<Todo> findCompletedTasks()  throws SQLException {
        Connection con= DBManager.getConnection();
        List<Todo> todoList=new LinkedList<>();

        String sql="select * from todo where isfinish=true";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            todoList.add(mapToTodo(rs));
        }
        DBManager.closeConnection(con);
        return todoList;
    }

    @Override
    public void markAsCompleted(int id)  throws SQLException {
        Connection con= DBManager.getConnection();
        String sql="update todo set isfinish=true where id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,id);
        int row=stmt.executeUpdate();
        System.out.println("Rows updated:"+row);
        DBManager.closeConnection(con);
    }

    @Override
    public int countTasks()  throws SQLException {
        Connection con= DBManager.getConnection();

        int cnt=0;
        String sql="select count(*) from todo";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            cnt=rs.getInt(1);
        }
        DBManager.closeConnection(con);
        return cnt;
    }

    @Override
    public void groupByStatus()  throws SQLException {
        Connection con= DBManager.getConnection();

        String sql="select isfinish,count(*) as count from todo group by isfinish";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getBoolean(1)+" "+rs.getInt(2));
        }
        DBManager.closeConnection(con);

    }
}
