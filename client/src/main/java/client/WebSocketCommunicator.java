package client;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.CreateBoard;
//import ui.GameplayREPL;
import websocket.messages.Error;
import websocket.messages.Notification;
import websocket.messages.LoadGame;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import static ui.EscapeSequences.ERASE_LINE;
import static ui.EscapeSequences.moveCursorToLocation;

public class WebSocketCommunicator extends Endpoint {
    Session session;

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
        } catch(DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception();
        }
    }

    private void handleMessage(String message) {
        if(message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            Notification notify = new Gson().fromJson(message, Notification.class);
            printNotification(notify.getMessage());
        }
    }

    private void printNotification(String message) {
        System.out.print(ERASE_LINE + '\r');
        System.out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
