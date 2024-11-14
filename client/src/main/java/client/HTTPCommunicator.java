package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import ui.CreateBoard;

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
        var jsonBody = gson.toJson(body);

        Map resp = request("POST", "/user", jsonBody);
        //System.out.println("Server response: " + resp);

        if (resp == null || resp.containsKey("Error:")) {
            System.err.println("Registration failed: " + resp.get("Error"));
            return false;
        }

        if (resp.containsKey("Error") || (resp.containsKey("message") && ((String) resp.get("message")).toLowerCase().contains("error"))) {
            System.err.println("Error registering user: " + resp.get("Error") + " or " + resp.get("message"));
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
            System.err.println("Error creating game: " + resp.get("Error"));
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
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error") || (resp.containsKey("message") && ((String) resp.get("message")).toLowerCase().contains("error"))) {
            System.err.println("Error registering user: " + resp.get("Error") + " or " + resp.get("message"));
            return false;
        }
        myFacade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public boolean logout() {
        Map<String, Object> resp = request("DELETE", "/session", null);
        if(resp == null) {
            myFacade.setAuthToken(null);
            return true;
        }

        if (resp.containsKey("Error") || (resp.containsKey("message") && ((String) resp.get("message")).toLowerCase().contains("error"))) {
            System.err.println("Error registering user: " + resp.get("Error") + " or " + resp.get("message"));
            return false;
        }

        myFacade.setAuthToken(null);
        return true;
    }

    public boolean joinGame(int ID, String color) {
        if(ID == 0) {
            return false;
        }
        Map body;
        if (color != null) {
            body = Map.of("gameID", ID, "playerColor", color);
        } else {
            body = Map.of("gameID", ID);
        }
        var jsonBody = new Gson().toJson(body);
        Map resp = request("PUT", "/game", jsonBody);
        return true;
    }

    public void observeGame() {
        Map<String, Object> resp = request("PUT", "/game", null);
        if(resp == null) {
            return;
        }

        CreateBoard board = new CreateBoard();
        board.printBoard(null);
    }



    private Map<String, Object> request(String method, String endpoint, String body) {
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);

            int responseCode = http.getResponseCode();
            //System.out.println("HTTP Response Code: " + responseCode);

            InputStream responseStream = (responseCode >= 200 && responseCode < 300)
                    ? http.getInputStream()
                    : http.getErrorStream();

            if (responseStream == null) {
                System.err.println("Error: Response stream is null.");
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(responseStream)) {
                Map<String, Object> respMap = gson.fromJson(reader, Map.class);
                //System.out.println("HTTP Response: " + respMap);
                return respMap;
            }
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
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
