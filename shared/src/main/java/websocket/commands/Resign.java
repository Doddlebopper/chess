package websocket.commands;

public class Resign extends UserGameCommand {

    int gameID;

    public Resign(String authToken, int gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }

    public int getID() {
        return gameID;
    }
}
