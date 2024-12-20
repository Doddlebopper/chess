package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDAOTests {

    private GameService gameService;
    private UserService userService;
    private String authToken;
    private GameDAO gameDao;
    private AuthDAO authDao;
    private UserDAO userDao;

    @BeforeEach
    public void setup() throws Exception {
        gameDao = new SQLGameDAO();
        authDao = new SQLAuthDAO();
        userDao = new SQLUserDAO();

        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao, userService);

        UserData user = new UserData("testUser", "password123", "test@example.com");
        AuthData authData = userService.register(user);
        authToken = authData.getToken();
    }
    @AfterEach
    public void stop() {
        gameService.clear();
        userService.clear();
    }

    @Test
    @DisplayName("Create and Retrieve User")
    public void createUserTest() throws DataAccessException {
        UserData user = userDao.getUser("testUser");
        assertNotNull(user, "ya failed retrieving the user");
        assertEquals("testUser", user.username(), "the usernames should match ya dingus");
        assertEquals("test@example.com", user.email(), "the emails should match ya dingus");
    }

    @Test
    @DisplayName("Authenticate the user")
    public void authenticateUser() throws DataAccessException {
        boolean isAuthenticated = userDao.authenticate("testUser","password123");
        assertTrue(isAuthenticated, "IT DIDN'T AUTHENTICATE CORRECTLY!!!");
    }

    @Test
    @DisplayName("Create and Retrieve Game")
    public void createAndRetrieveGameTest() throws UnauthorizedException, DataAccessException {
        int gameID = gameService.createGame(authToken, "Test Game");
        GameData retrievedGame = gameDao.getGame(gameID);
        assertNotNull(retrievedGame, "THE GAME DIDNT GET RETRIEVED ");
        assertEquals("Test Game", retrievedGame.getName(), "THEIR NAMES DIDNT MATCH!!!!");
    }

    @Test
    @DisplayName("Delete Game")
    public void deleteGameTest() throws DataAccessException, UnauthorizedException {
        int gameID = gameService.createGame(authToken, "testDeleteGame");
        gameDao.deleteGame(gameID);
        assertThrows(DataAccessException.class, () -> gameDao.getGame(gameID), "WHY WAS THE GAME FOUND! YOU IDIOT! DELETE THE GAME!");
    }

    @Test
    @DisplayName("Clear Database")
    public void clearDatabaseTest(){
        gameService.clear();
        userService.clear();
        assertTrue(gameDao.listGames().isEmpty(), "WHY ARENT THE TABLES EMPTY??");
    }

    @Test
    @DisplayName("Register user with existing username")
    public void registerUserWithExistingUsername() throws DataAccessException, BadRequestException {
        UserData user = new UserData("duplicateUser", "password", "user@example.com");
        userService.register(user);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(user);
        });

        assertEquals("Cannot register the same user twice", exception.getMessage(), "WHERE WAS THE ERROR? NO ERROR? NO GIRLFRIEND? GET A LIFE!");
    }

    @Test
    @DisplayName("Login with non-existing user")
    public void loginNonExistingUser() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("nonExistingUser", "password");
        });

        assertEquals("User not found", exception.getMessage(), "INVALID USER NAME AND PASSWORD ERROR! UH OH ! ");
    }

    @Test
    @DisplayName("Create game with invalid auth token")
    public void createGameWithInvalidAuthToken() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame("invalidAuthToken", "Test Game");
        });

        assertEquals(null, exception.getMessage(), "WRONG AUTH TOKEN! FIX IT NOW!");
    }

    @Test
    @DisplayName("Join game with invalid color")
    public void joinGameWithInvalidColor() throws Exception {
        int gameID = gameService.createGame(authToken, "Test Game");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(authToken, gameID, "INVALID_COLOR");
        });

        assertEquals("Invalid color", exception.getMessage(), "WHERE WAS THE INVALID ERROR? NO ERROR? NO LIFE? YOU SUCK AT EVERYTHING!");
    }

    @Test
    @DisplayName("Update Existing Game - Success")
    public void updateExistingGame() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData originalGame = new GameData(1, "Player1", "Player2", "TestGame", chessGame);
        gameDao.createGame(originalGame);

        GameData updatedGame = new GameData(1, "UpdatedPlayer1", "UpdatedPlayer2", "UpdatedGameName", chessGame);
        gameDao.updateGame(updatedGame);

        GameData retrievedGame = gameDao.getGame(1);
        assertEquals("UpdatedPlayer1", retrievedGame.whiteUsername(), "White username was not updated correctly");
        assertEquals("UpdatedPlayer2", retrievedGame.blackUsername(), "Black username was not updated correctly");
        assertEquals("UpdatedGameName", retrievedGame.gameName(), "Game name was not updated correctly");
    }

    @Test
    @DisplayName("Update Non-Existing Game - Failure")
    public void updateNonExistingGame() {
        ChessGame chessGame = new ChessGame();
        GameData nonExistingGame = new GameData(999, "NonExistingPlayer1", "NonExistingPlayer2", "NonExistingGameName", chessGame);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameDao.updateGame(nonExistingGame);
        });

        assertEquals("Item requested to be updated not found", exception.getMessage(), "Exception message did not match expected");
    }













}
