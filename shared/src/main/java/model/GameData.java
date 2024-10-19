package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData {
        if (whiteUsername == null || whiteUsername.isEmpty()) {
            throw new IllegalArgumentException("White username cannot be null or empty.");
        }
        if (blackUsername == null || blackUsername.isEmpty()) {
            throw new IllegalArgumentException("Black username cannot be null or empty.");
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty.");
        }
    }

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
