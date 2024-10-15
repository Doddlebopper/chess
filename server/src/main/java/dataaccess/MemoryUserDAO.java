package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO {
    private final Map<String, UserData> storeUser = new HashMap<>();

}
