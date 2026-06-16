package postgresIntro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class postgresInsert {
    public static void main(String[] args) {
        //        define url,user,password
        String url="jdbc:postgresql://localhost:5432/northernarc";
        String user="postgres";
        String password="12345";

//        try with resources

//        dml queries -> use executeUpdate
        try(Connection conn= DriverManager.getConnection(url,user,password)){
            System.out.println("Connected to database successfully.");

            String sql2="Insert into person(name,email) values('yuva','yuva@gmail.com'),('gokul','gokul@gmail.com')";
            PreparedStatement stmt2=conn.prepareStatement(sql2);
            int data=stmt2.executeUpdate();
            System.out.println(data);

            String sql3="Update person set name='siva' where id=1 ";
            PreparedStatement stmt3=conn.prepareStatement(sql3);
            System.out.println("Executing "+stmt3.executeUpdate());

            String sql4="Delete from person where name not like 'siva'";
            PreparedStatement stmt4=conn.prepareStatement(sql4);
            System.out.println("Deleting "+stmt4.executeUpdate());

        } catch (SQLException e) {
            System.out.println("Failed to connected to the database.");
            e.printStackTrace();
        }
        }
}
