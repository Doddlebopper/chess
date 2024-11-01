package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.sql.*;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            var conn = DatabaseManager.getConnection(); {
                var createTestTable = """            
                    CREATE TABLE if NOT EXISTS game (
                                    gameID INT NOT NULL,
                                    whiteUsername VARCHAR(255),
                                    blackUsername VARCHAR(255),
                                    gameName VARCHAR(255),
                                    chessGame TEXT,
                                    PRIMARY KEY (gameID)
                                    )""";
                try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                    createTableStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")) {
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = gameDeserialize(results.getString("chessGame"));
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                    else {
                        throw new DataAccessException("Game not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Game not found, id: " + gameID);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try(var stmnt=conn.prepareStatement("INSERT INTO game(gameID, whiteUsername, blackUsername, gameName, chessGame)VALUES(?, ?, ?, ?, ?)"))
            {
                stmnt.setInt(1, game.gameID());
                stmnt.setString(2, game.whiteUsername());
                stmnt.setString(3, game.blackUsername());
                stmnt.setString(4, game.gameName());
                stmnt.setString(5, gameSerialize(game.game()));
                stmnt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("error inserting data");
        }
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE FROM game WHERE gameID = ?")) {
                statement.setInt(1, gameID);
                int affectedRows = statement.executeUpdate();

                if(affectedRows == 0) {
                    throw new DataAccessException("No game found to delete");
                }
            }
        }
        catch(SQLException e) {
            throw new DataAccessException("Error deleting game");
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?")) {
                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, gameSerialize(game.game()));
                statement.setInt(5, game.gameID());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Item requested to be updated not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't find item to update");
        }
    }

    @Override
    public HashSet<GameData> listGames() {
        HashSet<GameData> games = new HashSet<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game")) {
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        var gameID = results.getInt("gameID");
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = gameDeserialize(results.getString("chessGame"));
                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Error listing games:");
        }
        return games;
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE game")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Clear operation error");
        }
    }

    @Override
    public void assignWhitePlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET whiteUsername = ? WHERE gameID = ?")) {
                statement.setString(1, username);
                statement.setInt(2, gameID);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Unable to assign white player");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating white player");
        }
    }

    @Override
    public void assignBlackPlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET blackUsername = ? WHERE gameID = ?")) {
                statement.setString(1, username);
                statement.setInt(2, gameID);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Unable to assign black player, hehe");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating black player");
        }
    }

    private static final Gson GSON = new Gson();

    private String gameSerialize(ChessGame game) {
        return GSON.toJson(game);
    }

    private ChessGame gameDeserialize(String game) {
        return GSON.fromJson(game, ChessGame.class);
    }
}
