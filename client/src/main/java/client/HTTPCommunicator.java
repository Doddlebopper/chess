package client;

import com.google.gson.Gson;
import model.GameData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;

public class HTTPCommunicator {
    private final String url;
    private final ServerFacade myFacade;
    private final Gson gson = new Gson();

    public HTTPCommunicator(ServerFacade facade, String domain) {
        this.url = "http://" + domain;
        this.myFacade = facade;
    }

    public static class ListGames {
        private HashSet<GameData> games;

        public HashSet<GameData> getGames() {
            return games;
        }
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var json = gson.toJson(body);
        Map<String, Object> resp = request("POST", "/user", json);
        if (resp.containsKey("Error")) {
            return false;
        }
        myFacade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        Map<String, Object> resp = request("POST", "/game", jsonBody);
        if (resp.containsKey("Error")) {
            return -1;
        }
        double gameID = (double) resp.get("gameID");
        return (int) gameID;
    }

    public HashSet<GameData> listGames() {
        String resp = getString("GET", "/game",null);
        if (resp.contains("Error")) {
            return HashSet.newHashSet(8);
        }
        ListGames games = new Gson().fromJson(resp, ListGames.class);

        return games.getGames();
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = gson.toJson(body);
        Map<String, Object> resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        myFacade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public boolean logout() {
        Map<String, Object> resp = request("DELETE", "/session", null);
        if (resp.containsKey("Error")) {
            return false;
        }
        myFacade.setAuthToken(null);
        return true;
    }

    public boolean joinGame(int gameID, String playerColor) {
        Map<String, Object> body;
        if (playerColor != null) {
            body = Map.of("gameID", gameID, "playerColor", playerColor);
        } else {
            body = Map.of("gameID", gameID);
        }
        var jsonBody = new Gson().toJson(body);
        Map<String, Object> resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }



    private Map<String, Object> request(String method, String endpoint, String body) {
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);

            int responseCode = http.getResponseCode();
            InputStream responseStream = (responseCode >= 200 && responseCode < 300)
                    ? http.getInputStream()
                    : http.getErrorStream();

            try (InputStreamReader reader = new InputStreamReader(responseStream)) {
                return gson.fromJson(reader, Map.class);
            }

        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }
    }

    private String getString(String method, String endpoint, String body) {
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);
            if (http.getResponseCode() == 401) {
                return "Error: 401";
            }
            try (InputStream respBody = http.getInputStream()) {
                return new BufferedReader(new InputStreamReader(respBody)).lines()
                        .reduce("", String::concat);
            }
        } catch (URISyntaxException | IOException e) {
            return String.format("Error: %s", e.getMessage());
        }
    }

    private String toString(InputStreamReader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int ch; (ch = reader.read()) != -1; ) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }

    }

    private HttpURLConnection makeConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {
        if(endpoint == null || !endpoint.startsWith("/")) {
            throw new IllegalArgumentException("Invalid endpoint, Must start with '/'");
        }

        URI uri = new URI(url + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        if (myFacade.getAuthToken() != null) {
            http.addRequestProperty("Authorization", myFacade.getAuthToken());
        }

        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }

        http.connect();
        return http;
    }
}
