package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> storeAuth = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData authData = storeAuth.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid token");
        }
        return authData;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (storeAuth.containsKey(auth.getToken())) {
            throw new DataAccessException("Token already exists");
        }
        storeAuth.put(auth.getToken(), auth);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (storeAuth.remove(authToken) == null) {
            throw new DataAccessException("Token not found");
        }
        else {
            storeAuth.remove(authToken);
        }
    }

    @Override
    public void clear() {
        storeAuth.clear();
    }
}