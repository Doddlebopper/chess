package service;

import model.AuthData;
import model.UserData;
import model.GameData;

public class UserService {
    public AuthData register(UserData user) {
        return new AuthData("stringToken", "nicSamson"); //temp
    }

    public AuthData login(UserData user) {
        return new AuthData("stringToken","nicSamson"); //temp

    }

    public void logout(AuthData auth) {}
}
