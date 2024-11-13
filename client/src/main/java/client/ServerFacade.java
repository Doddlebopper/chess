package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import ui.CreateBoard;

import java.util.HashSet;

public class ServerFacade {
    HTTPCommunicator http;
    String domain;
    String authToken;

    public ServerFacade() throws Exception {
        this("localhost:8080");
    }

    public ServerFacade(String domain) {
        this.domain = domain;
        http = new HTTPCommunicator(this, domain);
    }

    public boolean register(String username, String password, String email) {
        return http.register(username, password, email);
    }

    public boolean login(String username, String password) {
        return http.login(username, password);
    }

    public boolean logout() {
        return http.logout();
    }

    public int createGame(String name) {
        return http.createGame(name);
    }

    public HashSet<GameData> listGames() {
        return http.listGames();
    }

    public boolean joinGame(int gameID, String playerColor) {
        return http.joinGame(gameID, playerColor);
    }

    public void playGame() {
        CreateBoard board = new CreateBoard();
        board.printBoard(null);
    }

    public void joinObserver() {
        CreateBoard board = new CreateBoard();
        board.printBoard(null);
    }




    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
