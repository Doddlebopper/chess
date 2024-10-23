package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) throws DataAccessException {
        if(users.containsKey(user.getName())) {
            throw new DataAccessException("Already Exists");
        }
        users.put(user.getName(),user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public boolean authenticate(String username, String password) throws DataAccessException {
        UserData user = users.get(username);
        if(user == null) {
            throw new DataAccessException("Not Found");
        }
        return user.getPass().equals(password);
    }

    public void clear() {
        users.clear();
    }

}
