package dataaccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    private final HashSet<GameData> games = new HashSet<>();

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.stream()
                .filter(game -> game.gameID() == gameID)
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Game not found."));
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.contains(game)) {
            throw new DataAccessException("Game already exists.");
        }
        games.add(game);
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        GameData game = getGame(gameID);
        games.remove(game);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        deleteGame(game.gameID());
        createGame(game);
    }

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(games); //copy
    }

    @Override
    public void clear() {
        games.clear();
    }

    public void assignWhitePlayer(int gameID, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = new GameData(game.getID(), username, game.getBlackUsername(), game.getName(), game.game());
        updateGame(updatedGame);
    }

    public void assignBlackPlayer(int gameID, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = new GameData(game.getID(), game.getWhiteUsername(), username, game.getName(), game.game());
        updateGame(updatedGame);
    }
}
