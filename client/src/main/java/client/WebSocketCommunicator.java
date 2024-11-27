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

public class WebSocketCommunicator {
}
