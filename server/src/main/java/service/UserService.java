package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public AuthData register(UserData user) throws DataAccessException, BadRequestException {
        UserData existingUser = userDao.getUser(user.getName());
        if(existingUser != null) {
            if(existingUser.getName().equals(user.getName())) {
                throw new DataAccessException("Cannot register the same user twice");
            }
            throw new BadRequestException("user already exists");
        }
        if(user.getPass() == null || user.getPass().isEmpty()) {
            throw new BadRequestException("Password required");
        }
        userDao.createUser(user);

        AuthData authData = new AuthData(generateToken(), user.getName());
        authDao.createAuth(authData);
        return authData;
    }

    public AuthData login(String username, String password) throws UnauthorizedException, DataAccessException{
        if(userDao.authenticate(username, password)) {
            AuthData authData  = new AuthData(generateToken(), username);

            authDao.createAuth(authData);
            return authData;
        }
        else {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData authData = authDao.getAuth(authToken);
        if(authData == null) {
            throw new UnauthorizedException("token not found");
        }
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
