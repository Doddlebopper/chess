package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {
    private final int gameID;

    private final String role;

    public Connect(String authToken, int gameID, ChessGame.TeamColor color, String role) {
        super(authToken);
        this.commandType = CommandType.CONNECT;
        this.gameID = gameID;
        this.playerColor = color;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public int getID() {
        return gameID;
    }

    public ChessGame.TeamColor getColor() {
        return playerColor;
    }
}
