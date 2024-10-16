package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    GameData insert(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void joinGame(int gameID, String username);
    void updateGame(GameData game) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
}
