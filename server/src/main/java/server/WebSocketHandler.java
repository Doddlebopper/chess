package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.Connect;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@WebSocket
public class WebSocketHandler {

    private static final Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());

    @OnWebSocketConnect
    public void onConnect(Session session) {
        Server.sessions.put(session, 0);
        LOGGER.info("WebSocket connection established: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String message) {
        Server.sessions.remove(session);

        LOGGER.info("WebSocket closed: " + session.getRemoteAddress().getAddress() +
                " | Status: " + status + " | Message: " + message);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("got: %s", message);

        if(message.contains("\"commandType\":\"JOIN_PLAYER\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.sessions.replace(session, command.getID());
            handleJoin(session, command);
        }
        else if (message.contains("\"commandType\":\"JOIN_OBSERVER\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.sessions.replace(session, command.getID());
            handleObserve(session, command);
        }
    }

    private void handleJoin(Session session, Connect command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());

            ChessGame.TeamColor color = command.getColor().toString().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            boolean rightColor;
            if (color == ChessGame.TeamColor.WHITE) {
                rightColor = Objects.equals(game.whiteUsername(), auth.username());
            } else {
                rightColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!rightColor) {
                Error error = new Error("Error: wrong color");
                sendError(session, error);
            }

            Notification notify = new Notification("%s has joined the game as %s".formatted(auth.username(), command.getColor().toString()));
            broadcastMessageExceptCurr(session, notify);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: not a valid game"));
        }
    }

    private void handleObserve(Session session, Connect command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());

            Notification notify = new Notification("%s has joined the game as an observer".formatted(auth.username()));
            broadcastMessageExceptCurr(session, notify);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Not a valid game"));
        }
    }

    public void broadcastMessageExceptCurr(Session currSession, ServerMessage message) throws IOException {
        broadcastMessageAll(currSession, message, false);
    }

    public void broadcastMessageAll(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        for (Session session : Server.sessions.keySet()) {
            boolean inGame = Server.sessions.get(session) != 0;
            boolean sameGame = Server.sessions.get(session).equals(Server.sessions.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return null;
        }
    }

}
