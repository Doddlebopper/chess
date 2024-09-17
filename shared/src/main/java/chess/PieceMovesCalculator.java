package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;

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
        Collection<ChessMove> moves = new HashSet<>();
        int[][] kingDirections = {
                {-1,-1}, {-1,0},{-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}
        };

        for(int[] dir : kingDirections) {
            int nextRow = position.getRow() + dir[0];
            int nextCol = position.getColumn() + dir[1];

            if(nextRow >= 1 && nextRow <= 8 && nextCol >= 1 && nextCol <= 8) {
                ChessPosition newPosition = new ChessPosition(nextRow, nextCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                if(newPiece == null || !newPiece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                    moves.add(new ChessMove(this.position, newPosition, null));
                }
            }
        }



        return moves;
    }

    private Collection<? extends ChessMove> QueenMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> BishopMovesCalculator() {
        for(int i = position.getRow(); i <= 8; i++) {
            for(int j = position.getColumn(); j <= 8; j++) {
            }
        }
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
