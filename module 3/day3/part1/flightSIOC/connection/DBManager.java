package flightSIOC.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private String url;
    private String user;
    private String password;
    public DBManager(String url, String user, String password){
        this.url=url;
        this.user=user;
        this.password=password;
    }
    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,user,password);
    }
    public void closeConnection(Connection con) throws SQLException{
        if(con!=null){
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("DB closing issues"+e.getMessage());
            }
        }
    }
}
