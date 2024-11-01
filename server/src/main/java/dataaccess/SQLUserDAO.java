package dataaccess;

import model.UserData;

import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS user (
                                    username VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255),
                                    PRIMARY KEY (username)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                statement.setString(1, user.username());
                statement.setString(2, encryptPass(user.password()));
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("User already exists: " + user.username());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    if(results.next()) {
                        var password = results.getString("password");
                        var email = results.getString("email");
                        return new UserData(username, password, email);
                    }
                    else {
                        throw new DataAccessException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found: " + username);
        }
    }

    @Override
    public boolean authenticate(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        if(user == null || user.getPass() == null) {
            throw new DataAccessException("User not found");
        }
        return matchPass(password, user.getPass());
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE user")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Clear operation error");
        }
    }

    private String encryptPass(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean matchPass(String password, String hashpassword) {
        return BCrypt.checkpw(password, hashpassword);
    }
}
