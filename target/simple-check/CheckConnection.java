import com.hotel.config.DBConnect;
import java.sql.*;

public class CheckConnection {
    public static void main(String[] args) {
        try (Connection con = DBConnect.dbCon()) {
            System.out.println("Database connection successful.");
            try (Statement ps = con.createStatement();
                 ResultSet rs = ps.executeQuery("SELECT COUNT(*) FROM rooms")) {
                rs.next();
                System.out.println("Rooms: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Error: " + e.getMessage());
        }
    }
}
