package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import dataaccess.DataAccessException;
import java.util.UUID;

import java.util.HashSet;

public class GameService {

    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final UserService userService;

    public GameService(GameDAO gameDao, AuthDAO authDao, UserService userService) {
        this.gameDao = gameDao;
        this.authDao = authDao;
        this.userService = userService;
    }

    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            authDao.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Invalid username or password");
        }
        return gameDao.listGames();
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        AuthData authData;
        try {
            authData = authDao.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid username or password");
        }

        ChessGame newChessGame = new ChessGame();
        int gameID = generateGameID();
        GameData newGame = new GameData(gameID, authData.username(), null, gameName, newChessGame);

        gameDao.createGame(newGame);
        return gameID;
    }

    public void joinGame(String authToken, int gameID, String color) throws UnauthorizedException, DataAccessException, BadRequestException {
        AuthData authData;
        try {
            authData = authDao.getAuth(authToken);
        } catch(DataAccessException e) {
            throw new BadRequestException("Invalid username or password");
        }

        GameData gameData;

        try {
            gameData = gameDao.getGame(gameID);
            if(gameData == null) {
                throw new BadRequestException("game not found");
            }
        }catch (DataAccessException e) {
            throw new BadRequestException("can't find game");
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
    }


    public void clear() {
        gameDao.clear();
        authDao.clear();
        userService.clear();
    }

    private int generateGameID() {
        return Math.abs(UUID.randomUUID().hashCode());
    }
}
