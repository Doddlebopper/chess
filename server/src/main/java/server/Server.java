package server;

import com.google.gson.JsonObject;
import dataaccess.*;
import model.GameData;
import spark.*;
import service.UserService;
import service.GameService;
import model.UserData;
import model.AuthData;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    GameDAO sqlGame;
    UserDAO sqlUser;
    AuthDAO sqlAuth;

    private static final Gson GSON = new Gson();
    static UserService userService;
    static GameService gameService;

    static ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();

    public Server(GameDAO gameDao, AuthDAO authDao, UserDAO userDao) {
        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao, userService);
    }

    public Server() {
        GameDAO gameDao = new SQLGameDAO();
        AuthDAO authDao = new SQLAuthDAO();
        UserDAO userDao = new SQLUserDAO();
        this.userService = new UserService(userDao, authDao);
        this.gameService = new GameService(gameDao, authDao, userService);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", WebSocketHandler.class);

        registerEndPoints();

        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(GSON.toJson(new ErrorResponse("Internal Server Error: " + exception.getMessage())));
        });
        
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void registerEndPoints() {
        Spark.post("/user", this::registerHandler);
        Spark.post("/session",this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game",this::getGamesHandler);
        Spark.post("/game",this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.delete("/db", this::clear);
    }

    private Object clear(Request request, Response response) {
        try {
            gameService.clear();
            userService.clear();

            response.status(200);
            return "{}";
        }
        catch(Exception e) {
            response.status(500);
            return GSON.toJson(new ErrorResponse(e.getMessage()));
        }
    }
    public void clearForTesting() {
        gameService.clear();
        userService.clear();
    }

    private Object joinGameHandler(Request request, Response response) throws UnauthorizedException, BadRequestException, DataAccessException {
        try {

            String authToken = request.headers("Authorization");
            JsonObject requestBody = GSON.fromJson(request.body(), JsonObject.class);
            String playerColor;
            try {
                playerColor = requestBody.get("playerColor").getAsString();
            }
            catch(Exception e) {
                response.status(400);
                return GSON.toJson(new ErrorResponse("bad request"));
            }
            GameData game = GSON.fromJson(request.body(), GameData.class);
            response.status(200);
            response.type("application/json");
            gameService.joinGame(authToken, game.getID(), playerColor);
            return "";
        }
        catch(UnauthorizedException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("unauthorized"));
        }
        catch(BadRequestException e) {
            response.status(400);
            return GSON.toJson(new ErrorResponse("bad request"));
        }
        catch(DataAccessException e) {
            response.status(403);
            return GSON.toJson(new ErrorResponse("already taken"));
        }
    }

    private Object createGameHandler(Request request, Response response) {
        String authToken = request.headers("Authorization");
        GameData game = GSON.fromJson(request.body(), GameData.class);

        try {

            if(authToken == null || game == null || game.getName() == null || game.getName().isEmpty()) {
                throw new BadRequestException("Invalid game creation: missing requirements");
            }
            int gameID = gameService.createGame(authToken, game.getName());
            response.status(200);
            response.type("application/json");
            return GSON.toJson(new CreateGameResult(gameID));
        }
        catch(UnauthorizedException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("unauthorized"));
        }
        catch (BadRequestException e) {
            response.status(400);
            return GSON.toJson(new ErrorResponse("bad request"));
        }
        catch (Exception e) {
            response.status(500);
            return GSON.toJson(new ErrorResponse(e.getMessage()));
        }

    }

    private Object getGamesHandler(Request request, Response response) throws UnauthorizedException {
        String authToken = request.headers("authorization");

        try {
            response.status(200);
            response.type("application/json");
            var games = gameService.listGames(authToken);
            return GSON.toJson(new GamesResponse(games));
        }
        catch(UnauthorizedException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("unauthorized"));
        }
        catch (Exception e) {
            response.status(500);
            return GSON.toJson(new ErrorResponse(e.getMessage()));

        }
    }

    private Object logoutHandler(Request request, Response response) {
        String authToken = request.headers("Authorization");
        try {
            userService.logout(authToken);
            response.status(200);
            response.type("application/json");
            return "";
        }
        catch(UnauthorizedException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("unauthorized"));
        }
        catch(DataAccessException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("invalid"));
        }
        catch(Exception e) {
            response.status(500);
            return GSON.toJson(new ErrorResponse(e.getMessage()));
        }

    }

    private Object loginHandler(Request request, Response response) {
        UserData user = GSON.fromJson(request.body(), UserData.class);
        try {
            AuthData auth = userService.login(user.getName(), user.getPass());
            response.status(200);
            response.type("application/json");
            return GSON.toJson(auth);

        } catch (DataAccessException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("Invalid username or password"));
        } catch (UnauthorizedException e) {
            response.status(401);
            return GSON.toJson(new ErrorResponse("unauthorized"));
        }
    }

    private Object registerHandler(Request request, Response response) {
        UserData user = GSON.fromJson(request.body(), UserData.class);
        try {
            AuthData auth = userService.register(user);
            response.status(200);
            response.type("application/json");
            return GSON.toJson(auth);
        }
        catch(BadRequestException e) {
            response.status(400);
            return GSON.toJson(new ErrorResponse("bad request"));
        }
        catch(DataAccessException e) {
            response.status(403);
            return GSON.toJson(new ErrorResponse("already taken" + e));
        }
        catch(Exception e) {
            response.status(500);
            return GSON.toJson(new ErrorResponse(e.getMessage()));
        }
    }



    private static class ErrorResponse {
        String message;
        public ErrorResponse(String message) {
            this.message = "Error: " + message;
        }
    }

    private static class GamesResponse {
        HashSet<GameData> games;

        public GamesResponse(HashSet<GameData> games) {
            this.games = games;
        }
    }

    private static class CreateGameResult {
        int gameID;
        public CreateGameResult(int gameID) {
            this.gameID = gameID;
        }
    }
}
