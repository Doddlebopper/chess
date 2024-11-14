package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
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

}
