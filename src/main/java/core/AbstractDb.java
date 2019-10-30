package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDb {
    private Connection connection;
    public  AbstractDb(Connection connection){
        this.connection = connection;
    }

    public boolean excute(PreparedStatement preparedStatement) throws SQLException {
      return   preparedStatement.execute();
    }
}
