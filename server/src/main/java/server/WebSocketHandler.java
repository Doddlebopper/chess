package server;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        Server.sessions.put((spark.Session) session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String message) {
        Server.sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("%s", message);
        session.getRemote().sendString("Response :" + message);
    }

}
