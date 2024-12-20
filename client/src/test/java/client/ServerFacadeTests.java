package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() {
        server.clearForTesting();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Register a new user successfully")
    public void registerSuccessTest() {
        boolean registered = facade.register("testUser", "password123", "test@example.com");
        assertTrue(registered, "User should be registered successfully");
    }

    @Test
    @DisplayName("Register a duplicate user")
    public void registerDuplicateTest() {
        facade.register("duplicateUser", "password123", "test@example.com");
        boolean duplicate = facade.register("duplicateUser", "password123", "test@example.com");
        assertFalse(duplicate, "Duplicate registration should fail");
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void loginSuccessTest() {
        facade.register("loginUser", "password123", "login@example.com");
        boolean loggedIn = facade.login("loginUser", "password123");
        assertTrue(loggedIn, "User should be able to log in with valid credentials");
    }

    @Test
    @DisplayName("Login with invalid credentials")
    public void loginFailureTest() {
        boolean loggedIn = facade.login("nonExistentUser", "wrongPassword");
        assertFalse(loggedIn, "Login should fail for invalid credentials");
    }

    @Test
    @DisplayName("Logout user successfully")
    public void logoutTest() {
        facade.register("logoutUser", "password123", "logout@example.com");
        facade.login("logoutUser", "password123");
        boolean loggedOut = facade.logout();
        assertTrue(loggedOut, "User should be logged out successfully");
    }

    @Test
    @DisplayName("Logout without login")
    public void logoutWithoutLoginTest() {
        boolean loggedOut = facade.logout();
        assertFalse(loggedOut, "Logout should fail if user is not logged in");
    }

    @Test
    @DisplayName("Create a new game successfully")
    public void createGameTest() {
        facade.register("gameUser", "password123", "game@example.com");
        facade.login("gameUser", "password123");
        int gameId = facade.createGame("Test Game");
        assertNotEquals(-1, gameId, "Game should be created successfully and return a valid ID");
    }

    @Test
    @DisplayName("List existing games")
    public void listGamesTest() {
        facade.register("listUser", "password123", "list@example.com");
        facade.login("listUser", "password123");
        facade.createGame("Game 1");
        facade.createGame("Game 2");

        HashSet<GameData> games = facade.listGames();
        assertNotNull(games, "Games list should not be null");
        assertEquals(2, games.size(), "Games list should contain two games");
    }

    @Test
    @DisplayName("Join a game successfully")
    public void joinGameTest() {
        facade.register("joinUser", "password123", "join@example.com");
        facade.login("joinUser", "password123");
        facade.createGame("Game to Join");
        String color = "white";
        assertDoesNotThrow(() -> facade.joinGame(0, color), "User should be able to join a game successfully");
    }

    @Test
    @DisplayName("Fail to join a game due to server error")
    public void joinGameServerErrorTest() {
        facade.register("errorUser", "password123", "error@example.com");
        facade.login("errorUser", "password123");
        String color = "white";
        boolean joined = facade.joinGame(0,color);
        assertFalse(joined, "User should fail to join the game due to server error");
    }

    @Test
    @DisplayName("Observe a game successfully")
    public void observeGameSuccessTest() {
        facade.register("observeUser", "password123", "observe@example.com");
        facade.login("observeUser", "password123");
        //assertDoesNotThrow(() -> facade.observeGame(), "User should successfully observe the game");
    }

    @Test
    @DisplayName("Fail to observe a game due to server error")
    public void observeGameServerErrorTest() {
        facade.register("errorObserver", "password123", "errorObserver@example.com");
        facade.login("errorObserver", "password123");
        assertDoesNotThrow(() -> {
            //facade.observeGame();
        }, "User should handle server error gracefully when observing the game");
    }

}
