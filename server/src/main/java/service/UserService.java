package service;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.UserDAO;
import model.UserData;
import java.util.UUID;

public class UserService {
    UserDAO userDao;
    AuthDAO authDao;

    public AuthData register(UserData user) {
        return new AuthData(generateToken(), user.getName());
    }

    public static AuthData login(UserData user) {
        return new AuthData("stringToken",user.getName()); //temp
    }

    public static Object logout(AuthData auth) {
        //Logs out the user represented by the authToken
        return "{}";
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static void clear() {
        UserDAO.clear();
    }

}
