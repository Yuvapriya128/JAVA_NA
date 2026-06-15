package daoInJDBC.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

//   should not be modified
    public static final String url="jdbc:postgresql://localhost:5432/northernarc";
    public static final String user="postgres";
    public static final String password="12345";

//    connection
//    throws SQLException because it's checked exception
    public static Connection getConnection() throws SQLException{

            return DriverManager.getConnection(url,user,password);

    }

//    close connection
//    Or use try catch / throws because closing might cause error
    public static void closeConnection(Connection connection){
        if(connection != null){
            try{
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: "+e.getMessage());
            }
        }
    }


}
