package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) throws IllegalAccessException {
        super(CommandType.MAKE_MOVE, authToken, gameID, color);
        if(move == null) {
            throw new IllegalAccessException("Move cannot be null");
        }
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}
