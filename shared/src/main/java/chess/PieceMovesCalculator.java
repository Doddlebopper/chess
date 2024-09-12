package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {


    private final ChessPosition myPosition;
    private final ChessBoard board;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>();
    }
}
