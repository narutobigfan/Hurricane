package haven;

import java.sql.SQLException;

public abstract class DatabaseTable implements IDatabaseTable {
    public DatabaseTable() throws SQLException {
        init();
    }
}
