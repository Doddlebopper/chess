package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
