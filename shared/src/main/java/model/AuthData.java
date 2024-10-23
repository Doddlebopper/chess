package model;

public record AuthData(String authToken, String username) {
    public String getToken() {
        return authToken;
    }

    public String getName() {
        return username;
    }
}
