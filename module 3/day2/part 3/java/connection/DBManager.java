package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
//    jdbc:postgresql://localhost:5432/database_name
private final String url;
    private final String user;
    private final String password;

    public DBManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public  Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
    public  void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
