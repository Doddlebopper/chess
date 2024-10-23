package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.AuthData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardServiceTests {

    private GameService gameService;
    private UserService userService;
    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        GameDAO gameDao = new MemoryGameDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();

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
    @DisplayName("Create a new game")
    public void createGame() throws Exception {
        int gameID = gameService.createGame(authToken, "Test Game");

        assertTrue(gameID > 0, "Game ID should be generated properly");
    }

    @Test
    @DisplayName("List games with valid auth token")
    public void listGames() throws Exception {
        gameService.createGame(authToken, "Test Game");

        var games = gameService.listGames(authToken);

        assertEquals(1, games.size(), "One game should exist");
    }

    @Test
    @DisplayName("Join game with invalid color")
    public void joinGameWithInvalidColor() throws Exception {
        int gameID = gameService.createGame(authToken, "Test Game");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(authToken, gameID, "INVALID_COLOR");
        });

        assertEquals("Invalid color", exception.getMessage(), "Expected invalid color error");
    }

    @Test
    @DisplayName("Create game with invalid auth token")
    public void createGameWithInvalidAuthToken() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame("invalidAuthToken", "Test Game");
        });

        assertEquals(null, exception.getMessage(), "Expected invalid auth token error");
    }

    @Test
    @DisplayName("Register user with existing username")
    public void registerUserWithExistingUsername() throws DataAccessException, BadRequestException {
        UserData user = new UserData("duplicateUser", "password", "user@example.com");
        userService.register(user);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(user);
        });

        assertEquals("Cannot register the same user twice", exception.getMessage(), "Expected a user already exists error");
    }

    @Test
    @DisplayName("Login with non-existing user")
    public void loginNonExistingUser() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("nonExistingUser", "password");
        });

        assertEquals("Not Found", exception.getMessage(), "Expected invalid username/password error");
    }

}
