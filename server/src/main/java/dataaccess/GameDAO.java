package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {

    GameData getGame(int gameID) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    HashSet<GameData> listGames();

    void clear();

    void assignWhitePlayer(int gameID, String username) throws DataAccessException;

    void assignBlackPlayer(int gameID, String username) throws DataAccessException;
}
