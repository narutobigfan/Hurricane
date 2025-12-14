package haven;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CredentialsTable extends DatabaseTable {

    public CredentialsTable() throws SQLException {
    }

    public void init() throws SQLException {
        Database.connection.prepareStatement("CREATE TABLE IF NOT EXISTS Credentials (" +
                "username TEXT UNIQUE," +
                "password TEXT)").execute();
    }

    public synchronized void saveCredentials(String username, String password) {
        try {
            PreparedStatement statement = Database.connection.prepareStatement("INSERT INTO Credentials (username, password) VALUES(?,?) ON CONFLICT(username) DO UPDATE SET password=? WHERE username=?");
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, password);
            statement.setString(4, username);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void removeCredentials(String username) {
        try {
            PreparedStatement statement = Database.connection.prepareStatement("DELETE FROM Credentials WHERE username=?");
            statement.setString(1, username);
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getUsernames() {
        try {
            ArrayList<String> usernames = new ArrayList<>();
            PreparedStatement statement = Database.connection.prepareStatement("SELECT username FROM Credentials ORDER BY ROWID");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                usernames.add(result.getString(1));
            }
            return usernames;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public String getPassword(String username) {
        try {
            PreparedStatement statement = Database.connection.prepareStatement("SELECT password FROM Credentials WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            return result.next() ? result.getString(1) : "";
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
