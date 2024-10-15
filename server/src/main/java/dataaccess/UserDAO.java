package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user);
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    boolean authenticate(String username, String password);
}
