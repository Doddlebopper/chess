package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {


    public String getName() {
        return gameName;
    }

    public int getID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }


    @Override
    public String toString() {
        return String.format("{\"gameID\": %d, \"whiteUsername\": \"%s\", \"blackUsername\": \"%s\", \"gameName\": \"%s\"}",
                gameID, whiteUsername, blackUsername, gameName);
    }
}
