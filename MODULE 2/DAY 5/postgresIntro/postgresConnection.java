package postgresIntro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class postgresConnection {
    public static void main(String[] args) {
//        define url,user,password
        String url="jdbc:postgresql://localhost:5432/northernarc";
        String user="postgres";
        String password="12345";

//        try with resources

        try(Connection conn= DriverManager.getConnection(url,user,password)){
            System.out.println("Connected to database successfully.");

//            write queries here
            String sql="Create table if not exists person(id serial primary key,name varchar(20),email varchar(50))";
            PreparedStatement stmt=conn.prepareStatement(sql);
            System.out.println("Executing: "+sql);

            stmt.execute();

            System.out.println("Table created successfully.");

            //        ddl queries -> use execute
            String sql2="Insert into person(name,email) values('yuva','yuva@gmail.com'),('gokul','gokul@gmail.com')";
            PreparedStatement stmt2=conn.prepareStatement(sql2);
            int data=stmt2.executeUpdate();
            System.out.println(data);

        } catch (SQLException e) {
            System.out.println("Failed to connected to the database.");
            e.printStackTrace();
        }

    }
}
