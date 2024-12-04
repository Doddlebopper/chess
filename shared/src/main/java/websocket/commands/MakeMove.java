package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    ChessMove move;
    int gameID;

    public MakeMove(String authToken, int gameID, ChessMove move) throws IllegalAccessException {
        super(authToken);
        if(move == null) {
            throw new IllegalAccessException("Move cannot be null");
        }
        this.move = move;
        this.gameID = gameID;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public ChessMove getMove() {
        return move;
    }

    public int getID() {
        return gameID;
    }

}
