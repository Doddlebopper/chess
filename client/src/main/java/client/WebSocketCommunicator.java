package client;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.GamePlayREPL;
import websocket.commands.Connect;
import websocket.messages.Error;
import websocket.messages.Notification;
import websocket.messages.LoadGame;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.ERASE_LINE;

public class WebSocketCommunicator extends Endpoint {
    private Session session;

    public WebSocketCommunicator(String domain) throws Exception {
        try {
            URI uri = new URI("ws://" + domain + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("Failed to connect to WebSocket server.", ex);
        }
    }

    private void handleMessage(String message) {
        if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            Notification notify = new Gson().fromJson(message, Notification.class);
            printNotification(notify.getMessage());
        } else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            Error error = new Gson().fromJson(message, Error.class);
            printNotification(error.getError());
        } else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
            printGame(loadGame.getGame());
        }
    }

    private void printNotification(String message) {
        System.out.print(ERASE_LINE + '\r');
        System.out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    private void printGame(ChessGame game) {
        System.out.print(ERASE_LINE + "\r\n");
        GamePlayREPL.CreateBoard.newGame(game);
        GamePlayREPL.CreateBoard.generateBoard(GamePlayREPL.color, null);
        System.out.println("[IN-GAME] >>> ");
    }

    public void sendMessage(String message) {
        System.out.println("Sending message: " + message); // Log for debugging
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * Sends a CONNECT command to the server.
     *
     * @param authToken The authentication token for the user.
     * @param gameID    The ID of the game to connect to.
     * @param color     The player's color in the game (optional for observers).
     * @param role      The role of the user ("player" or "observer").
     */
    public void sendConnectMessage(String authToken, int gameID, ChessGame.TeamColor color, String role) {
        Connect connectCommand = new Connect(authToken, gameID, color, role);
        String json = new Gson().toJson(connectCommand);
        System.out.println("Sending CONNECT message: " + json);
        sendMessage(json);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection established.");
    }
}
