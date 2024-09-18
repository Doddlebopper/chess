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
        int[][] kingDirections = { {-1,-1}, {-1,0},{-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
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
        Collection<ChessMove> moves = new HashSet<>();

        int[][] queenDirections = { {-1,-1}, {-1,0},{-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

        for(int[] dir : queenDirections) {
            int nextRow = position.getRow();
            int nextCol = position.getColumn();

            while(true) {
                nextRow += dir[0];
                nextCol += dir[1];


                if(nextRow < 1 || nextRow > 8 || nextCol < 1 || nextCol > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(nextRow, nextCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    moves.add(new ChessMove(this.position, newPosition, null));
                }
                else {
                    if (!newPiece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                        moves.add(new ChessMove(this.position, newPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<? extends ChessMove> BishopMovesCalculator() {
        Collection<ChessMove> moves = new HashSet<>();
        int[][] bishopDirections = { {1,1}, {1,-1}, {-1,1}, {-1,-1}};

        for(int[] dir : bishopDirections) {
            int row = position.getRow() - 1;
            int col = position.getColumn() - 1;

            while (true) {
                row += dir[0];
                col += dir[1];

                if(row < 0 || row >= 8 || col < 0 || col >= 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(position, newPosition,null)); // Empty square
                } else if (!piece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                    moves.add(new ChessMove(position, newPosition,null)); // Capture opponent's piece
                    break; // Cannot move past the captured piece
                } else {
                    break; // Blocked by own piece
                }
            }
        }
        return moves;
    }

    private Collection<? extends ChessMove> KnightMovesCalculator() {
        return new ArrayList<>();
    }

    private Collection<? extends ChessMove> RookMovesCalculator() {
        Collection<ChessMove> moves = new HashSet<>();

        int[][] rookDirections = {{1,0},{0,1},{-1,0},{0,-1}};

        for(int[] dir : rookDirections) {
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if(row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    moves.add(new ChessMove(position, newPosition,null)); // Empty square
                } else if (!newPiece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                    moves.add(new ChessMove(position, newPosition,null)); // Capture opponent's piece
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<? extends ChessMove> PawnMovesCalculator() {
        return new ArrayList<>();
    }
}
