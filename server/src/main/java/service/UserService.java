package service;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.UserDAO;
import model.UserData;
import dataaccess.DataAccessException;
import java.util.UUID;

public class UserService {
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public AuthData register(UserData user) throws DataAccessException {
        userDao.createUser(user);

        AuthData authData = new AuthData(generateToken(), user.getName());
        authDao.createAuth(authData);
        return authData;
    }

    public AuthData login(String username, String password) throws DataAccessException{
        if(userDao.authenticate(username, password)) {
            AuthData authData  = new AuthData(generateToken(), username);
            authDao.createAuth(authData);
            return authData;
        }
        else {
            throw new DataAccessException("Invalid username or password");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authDao.deleteAuth(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
    }

}
