package daoImpl;

import connection.DBManager;
import dao.TodoDao;
import entity.Todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TodoDaoImplJdbc implements TodoDao {
    private final DBManager dbManager;

    public TodoDaoImplJdbc(DBManager dbManager) {
        this.dbManager = dbManager;
    }
    public  Todo mapTotodo(ResultSet rs) throws SQLException{
        return new Todo(rs.getInt(1),rs.getString(2),rs.getBoolean(3));
    }

    @Override
    public void save(Todo todo) throws SQLException {
        Connection con= dbManager.getConnection();
        String sql="Insert into todo(task,isfinish) values(?,?)";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,todo.getTask());
        stmt.setBoolean(2,todo.getIsFinish());
        int row=stmt.executeUpdate();
        System.out.println("Rows updated:"+row);

        con.close();

    }

    @Override
    public Todo findById(int id) throws SQLException {
        try(Connection con= dbManager.getConnection()) {
            String sql = "select * from todo where id=?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapTotodo(rs);
            }
        }catch (Exception e){
            System.out.println("db connectivity issues:"+e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<Todo> findAll() throws SQLException {
        List<Todo> todoList=new LinkedList<>();
        Connection con= dbManager.getConnection();
        String sql="select * from todo";
        PreparedStatement stmt=con.prepareStatement(sql);

        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            todoList.add(mapTotodo(rs));
        }
        con.close();
        return todoList;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        Connection con= dbManager.getConnection();
        String sql="delete from todo where id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,id);
        int row=stmt.executeUpdate();
        System.out.println("Rows deleted:"+row);

        con.close();
    }

    @Override
    public void updateById(int id, Todo todo) throws SQLException {
        Connection con= dbManager.getConnection();
        String sql="update todo set task=? ,isfinish=? where id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,todo.getTask());
        stmt.setBoolean(2,todo.getIsFinish());
        stmt.setInt(3,id);
        int row=stmt.executeUpdate();
        System.out.println("Rows updated:"+row);

        con.close();

    }
}
