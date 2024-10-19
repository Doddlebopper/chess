package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import dataaccess.DataAccessException;

import java.util.HashSet;

public class GameService {

    private final GameDAO gameDao;
    private final AuthDAO authDao;

    public GameService(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            authDao.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
        return gameDao.listGames();
    }

    public Object createGame(String gameName) {
        //FIX BELOW
        ChessGame newChessGame = new ChessGame();
        GameData newGame = new GameData(1, "white","black","ourGame",newChessGame);
        //FIX ABOVE
        //games.add(newGame);
        StringBuilder result = new StringBuilder("{\"gameID\": ");
        result.append(newGame.getID());
        return result;
    }

    public Object joinGame(String authToken, int gameID, String color) throws UnauthorizedException, DataAccessException, BadRequestException {
        AuthData authData;
        try {
            authData = authDao.getAuth(authToken);
        } catch(DataAccessException e) {
            throw new UnauthorizedException();
        }

        GameData gameData;

        try {
            gameData = gameDao.getGame(gameID);
            if(gameData == null) {
                throw new BadRequestException("game not found");
            }
        }catch (DataAccessException e) {
            throw new BadRequestException("bad request");
        }

        String whiteUser = gameData.getWhiteUsername();
        String blackUser = gameData.getBlackUsername();

        if(color.equals("WHITE")) {
            if(whiteUser != null) {
                throw new DataAccessException("White user already exists");
            }
            gameDao.assignWhitePlayer(gameID, authData.username());
        }
        else if(color.equals("BLACK")) {
            if(blackUser != null) {
                throw new BadRequestException("Black user already exists");
            }
            gameDao.assignBlackPlayer(gameID, authData.username());

        }
        else {
            throw new BadRequestException("Invalid color");
        }

        return "{}";
    }


    public void clear() {
        gameDao.clear();
    }
}
