package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> pieceMoves(ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        switch(piece.getPieceType()) {
            case KING:
                moves.addAll(KingMovesCalculator());
                break;
            case QUEEN:
                moves.addAll(QueenMovesCalculator());
                break;
            case BISHOP:
                moves.addAll(BishopMovesCalculator());
                break;
            case KNIGHT:
                moves.addAll(KnightMovesCalculator());
                break;
            case ROOK:
                moves.addAll(RookMovesCalculator());
                break;
            case PAWN:
                moves.addAll(PawnMovesCalculator());
                break;
        }
        return moves;
    }

    private Collection<? extends ChessMove> KingMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> QueenMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> BishopMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> KnightMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> RookMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> PawnMovesCalculator() {
        return new ArrayList<>();
    }
}
