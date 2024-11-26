package websocket.commands;

public class Leave extends UserGameCommand{
    int gameID;

    public Leave(String authToken, int gameID) {
        super(CommandType.LEAVE, authToken, gameID);

        this.gameID = gameID;
    }

    public int getID() {
        return gameID;
    }
}
