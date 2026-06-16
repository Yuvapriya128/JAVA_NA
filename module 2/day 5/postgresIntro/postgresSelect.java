package postgresIntro;

import java.sql.*;

public class postgresSelect {
    public static void main(String[] args) {
        String url="jdbc:postgresql://localhost:5432/northernarc";
        String user="postgres";
        String password="12345";

        try(Connection con=DriverManager.getConnection(url,user,password)){

            String sql="select * from person";
            PreparedStatement stmt=con.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("name")+" "+rs.getString("email"));
            }

        } catch (SQLException e) {
            System.out.println("Failed to connected to the database.");
            e.printStackTrace();
        }
    }
}
