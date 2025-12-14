package haven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection connection;

    public static CredentialsTable credentialsTable;

    static {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:" + Config.HOMEDIR.getPath() + "/custom.db");

            credentialsTable = new CredentialsTable();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Database.close()));
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
