package model;

public record UserData(String username, String password, String email) {
    public String getName() {
        return username;
    }

    public String getPass() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
