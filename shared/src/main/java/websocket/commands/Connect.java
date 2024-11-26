package websocket.commands;

public class Connect extends UserGameCommand {
    int gameID;

    public Connect(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);

        this.gameID = gameID;
    }

    public int getID() {
        return gameID;
    }
}
