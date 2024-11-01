package dataaccess;

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


}
