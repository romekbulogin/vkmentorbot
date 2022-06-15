import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn = null;
    public Connection connect() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://ec2-3-228-235-79.compute-1.amazonaws.com:5432/d8k9ke2c61h3hb", "olezekfprmlfip", "cf1b053ec8d0bcdec4e228e724d7a3b3058feade8ecf852579639523f37a0a08");
            //System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
