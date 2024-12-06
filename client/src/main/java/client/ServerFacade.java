package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.messages.ServerMessage;
import websocket.commands.*;

import java.util.HashSet;
import java.io.IOException;

public class ServerFacade {
    HTTPCommunicator http;
    WebSocketCommunicator ws;
    String domain;
    String authToken;

    public ServerFacade() throws Exception {
        this("localhost:8080");
        http = new HTTPCommunicator(this, domain);
    }

    public ServerFacade(String domain){
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

    public boolean joinGame(int ID, String color) {
        return http.joinGame(ID, color);
    }

    public void connectWS() {
        try {
            ws = new WebSocketCommunicator(domain);
        }
        catch (Exception e) {
            System.out.println("Failed to make connection");
        }

    }

    public void joinPlayer(int gameID, ChessGame.TeamColor color) {
        sendCommand(new Connect(authToken, gameID, color, "player"));
    }

    public void joinObserver(int gameID) {
        sendCommand(new Connect(authToken, gameID, ChessGame.TeamColor.WHITE, "observer"));
    }

    public void makeMove(int gameID, ChessMove move) throws IllegalAccessException {
        sendCommand(new MakeMove(authToken, gameID, move));
    }

    public void userLeave(int gameID) {
        sendCommand(new Leave(authToken, gameID));
    }

    public void userResign(int gameID) {
        sendCommand(new Resign(authToken, gameID));
    }

    public void sendCommand(UserGameCommand command) {
        String message = new Gson().toJson(command);
        ws.sendMessage(message);
    }




    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
