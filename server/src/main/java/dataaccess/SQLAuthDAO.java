package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS auth (
                                    username VARCHAR(255) NOT NULL,
                                    authToken VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                try (var results = statement.executeQuery()) {
                    if(results.next()) {
                        var username = results.getString("username");
                        return new AuthData(authToken, username);
                    }
                    else {
                        throw new DataAccessException("Auth token doesn't exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Auth Token does not exist");
        }

    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, auth.username());
                statement.setString(2, auth.authToken());
                statement.executeUpdate();

            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error creating auth token");

        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection() ) {
            try (var statement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                int rowsDeleted = statement.executeUpdate();
                if(rowsDeleted == 0) {
                    throw new DataAccessException("Auth token not found");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error deleting token");
        }
    }

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            }
            catch (SQLException e) {
                throw new DataAccessException("Error clearing auth table");
            }
        }catch (SQLException | DataAccessException e) {
            System.err.println("Unable to clear from database");
        }
    }
}
