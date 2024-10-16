package service;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    static List<GameData> games = new ArrayList<>();

    public static Object listGames() {
        if(games.isEmpty()) {
            return "{\"games\": []}";
        }
        else {
            StringBuilder result = new StringBuilder("{\"games\": [");

            for(int i = 0; i < games.size(); i++) {
                result.append(games.get(i).toString());
                if(i < games.size() - 1) {
                    result.append(", ");
                }
            }

            result.append("]}");
            return result.toString();
        }
    }

    public static Object createGame(String gameName) {
        //FIX BELOW
        ChessGame newChessGame = new ChessGame();
        GameData newGame = new GameData(1, "white","black","ourGame",newChessGame);
        //FIX ABOVE
        games.add(newGame);
        StringBuilder result = new StringBuilder("{\"gameID\": ");
        result.append(newGame.getID());
        return result;
    }

    public static Object joinGame(String playerColor, int gameID) {
        //verifies the game exists and adds the caller as the requested color to the game
        return "{}";
    }
}
