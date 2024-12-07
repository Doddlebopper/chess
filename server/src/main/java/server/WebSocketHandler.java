package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.messages.ErrorMessage;
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

        if(message.contains("\"commandType\":\"CONNECT\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);

            if(command.getRole() == null || command.getRole().isEmpty()) {
                command.setRole(command.getColor() != null ? "player" : "observer");
            }

            Server.sessions.replace(session, command.getID());

            if("player".equalsIgnoreCase(command.getRole())) {
                System.out.printf("Handling player join for session: %s%n", session.getRemoteAddress());
                handleJoin(session, command);
            }
            else if("observer".equalsIgnoreCase(command.getRole())) {
                System.out.printf("Handling observer join for session: %s%n", session.getRemoteAddress());
                handleObserve(session, command);
            }
            else {
                sendError(session, new ErrorMessage("Invalid role specified for CONNECT command"));
            }
        }
        else if(message.contains("\"commandType\":\"MAKE_MOVE\"")) {
            MakeMove command = new Gson().fromJson(message, MakeMove.class);
            handleMove(session, command);
        }
        else if(message.contains("\"commandType\":\"LEAVE\"")) {
            Leave command = new Gson().fromJson(message, Leave.class);
            handleLeave(session, command);
        }
        else if (message.contains("\"commandType\":\"RESIGN\"")) {
            Resign command = new Gson().fromJson(message, Resign.class);
            handleResign(session, command);
        }
    }

    private void handleLeave(Session session, Leave command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData currentGame = Server.gameService.getData(command.getAuthToken(), command.getID());

            GameData updatedGame = new GameData(
                    currentGame.gameID(),
                    Objects.equals(currentGame.whiteUsername(), auth.username()) ? null : currentGame.whiteUsername(),
                    Objects.equals(currentGame.blackUsername(), auth.username()) ? null : currentGame.blackUsername(),
                    currentGame.getName(),
                    currentGame.game()
            );

            Server.gameService.setGame(auth.authToken(), updatedGame);

            Notification notify = new Notification("%s has left the game.".formatted(auth.username()));
            broadcastMessageExceptCurr(session, notify);

            session.close();
        } catch (UnauthorizedException | BadRequestException e) {
            sendError(session, new ErrorMessage("Error: not authorized"));
        }
    }

    private void handleResign(Session session, Resign command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);

            String oppUsername = userColor == ChessGame.TeamColor.WHITE ? game.blackUsername() : game.whiteUsername();

            if (game.game().getGameOver()) {
                sendError(session, new ErrorMessage("Error: The game is already over!"));
                return;
            }

            if (userColor == null) {
                sendError(session, new ErrorMessage("Error: You are observing this game"));
                return;
            }


            game.game().setGameOver(true);
            Server.gameService.setGame(auth.authToken(), game);
            Notification notify = new Notification("%s has forfeited, %s wins!".formatted(auth.username(), oppUsername));
            broadcastMessageAll(session, notify, true);
        } catch (UnauthorizedException e) {
            sendError(session, new ErrorMessage("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new ErrorMessage("Error: invalid game"));
        }
    }

    private void handleJoin(Session session, Connect command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());
            if(game == null) {
                throw new BadRequestException("Game with ID " + command.getID() + " does not exist.");
            }

            ChessGame.TeamColor color = command.getColor().toString().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            boolean rightColor;
            if (color == ChessGame.TeamColor.WHITE) {
                rightColor = Objects.equals(game.whiteUsername(), auth.username());
            } else {
                rightColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!rightColor) {
                sendError(session, new ErrorMessage("Error: You are trying to join as the wrong color."));
                return;
            }

            Notification n = new Notification("%s has joined the game as %s".formatted(auth.username(), command.getColor().toString()));
            broadcastMessageExceptCurr(session, n);

            LoadGame load = new LoadGame(game.game());
            System.out.println("Sending LOAD_GAME message to: " + session.getRemoteAddress());
            sendMessage(session, load);
        } catch (UnauthorizedException e) {
            sendError(session, new ErrorMessage("Error: not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new ErrorMessage("Error: not a valid game"));

        }
    }

    private void handleObserve(Session session, Connect command) throws IOException, UnauthorizedException, BadRequestException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());
            if(game == null) {
                throw new BadRequestException("Game with ID " + command.getID() + " does not exist.");
            }

            Notification notify = new Notification("%s has joined the game as an observer".formatted(auth.username()));
            System.out.println("Observer notification: " + new Gson().toJson(notify));
            broadcastMessageExceptCurr(session, notify);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (UnauthorizedException e) {
            System.err.println("UnauthorizedException: " + e.getMessage());
            sendError(session, new ErrorMessage("Error: Not authorized"));
        } catch (BadRequestException e) {
            System.err.println("BadRequestException: " + e.getMessage());
            sendError(session, new ErrorMessage("Error: Not a valid game"));
        }
    }

    private void handleMove(Session session, MakeMove command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getData(command.getAuthToken(), command.getID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);

            if(userColor == null) {
                sendError(session, new ErrorMessage("Error: You are observing"));
                return;
            }

            if(game.game().getGameOver()) {
                sendError(session, new ErrorMessage("Error: the game is over"));
                return;
            }

            if(game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(command.getMove());

                Notification notify;
                ChessGame.TeamColor oppColor = userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if(game.game().isInCheckmate(oppColor)) {
                    notify = new Notification("Checkmate! %s wins!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if(game.game().isInStalemate(oppColor)) {
                    notify = new Notification("Stalemate! It's a tie!");
                }
                else if(game.game().isInCheck(oppColor)) {
                    notify = new Notification("%s is now in check!".formatted(auth.username()));
                }
                else {
                    notify = new Notification("%s has made a move!".formatted(auth.username()));
                }
                broadcastMessageExceptCurr(session, notify);

                Server.gameService.setGame(auth.authToken(), game);

                LoadGame load = new LoadGame(game.game());
                broadcastMessageAll(session, load, true);
            }
            else {
                sendError(session, new ErrorMessage("Error: not your turn"));
            }
        } catch (UnauthorizedException e) {
            sendError(session, new ErrorMessage("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new ErrorMessage("Error: invalid game"));
        } catch (InvalidMoveException e) {
            sendError(session, new ErrorMessage("Error: invalid move"));
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
        System.out.printf("Sending message to session %s: %s%n", session.getRemoteAddress(), new Gson().toJson(message));
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, ErrorMessage error) throws IOException {
        System.out.printf("Sending message to session %s: %s%n", session.getRemoteAddress(), new Gson().toJson(error));
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
