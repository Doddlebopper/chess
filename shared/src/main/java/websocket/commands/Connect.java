package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {
    int gameID;

    public Connect(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.commandType = CommandType.CONNECT;
        this.gameID = gameID;
        this.playerColor = color;
    }

    public int getID() {
        return gameID;
    }

    public ChessGame.TeamColor getColor() {
        return playerColor;
    }
}
