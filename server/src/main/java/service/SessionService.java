package service;

import model.AuthData;
import model.UserData;

public class SessionService {

    public static AuthData login(UserData user) {
        return new AuthData("stringToken",user.getName()); //temp
    }

    public static Object logout(AuthData auth) {
        //Logs out the user represented by the authToken
        return "{}";
    }

}
