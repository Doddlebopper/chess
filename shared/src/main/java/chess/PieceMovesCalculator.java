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
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if(row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
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
        Collection<ChessMove> moves = new HashSet<>();

        int[][] knightDirections = {{2,1},{2,-1}, {1,2},{-1,2},{-2,1},{-2,-1},{1,-2},{-1,-2}};

        for(int[] dir : knightDirections) {
            int row = position.getRow() + dir[0];
            int col = position.getColumn() + dir[1];

            if(row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece newPiece = board.getPiece(newPosition);

                if(newPiece == null || !newPiece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                    moves.add(new ChessMove(this.position, newPosition, null));
                }
            }
        }
        return moves;
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

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    moves.add(new ChessMove(position, newPosition, null)); // Empty square
                } else if (!newPiece.getTeamColor().equals(this.board.getPiece(position).getTeamColor())) {
                    moves.add(new ChessMove(position, newPosition, null)); // Capture opponent's piece
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<? extends ChessMove> PawnMovesCalculator() {
        Collection<ChessMove> moves = new ArrayList<>();

        int direction = board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;

        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition oneSquare = new ChessPosition(row + direction, col);
        if(row + direction >= 1 && row + direction <= 8 && board.getPiece(oneSquare) == null) {
            if(row + direction == 8 || row + direction == 1) {
                moves.addAll(generatePromotionMoves(position, oneSquare));
            }
            else {
                moves.add(new ChessMove(position, oneSquare, null));
            }

            if((board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                    (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {

                ChessPosition twoSquares = new ChessPosition(row + 2 * direction, col);
                if(board.getPiece(twoSquares) == null) {
                    moves.add(new ChessMove(position, twoSquares, null));
                }
            }

        }

        int newRow = row + direction;
        int newCol = col - 1;

        if(newCol >= 1 && newRow >= 1 && newRow <= 8) {
            ChessPosition LCapture = new ChessPosition(newRow, newCol);
            ChessPiece LPiece = board.getPiece(LCapture);
            if (LPiece != null && !LPiece.getTeamColor().equals(board.getPiece(position).getTeamColor())) {
                if (row + direction == 8 || row + direction == 1) {
                    moves.addAll(generatePromotionMoves(position, LCapture));
                } else {
                    moves.add(new ChessMove(position, LCapture, null));
                }
            }
        }

        ChessPosition RCapture = new ChessPosition(row + direction, col + 1);
        if (col + 1 <= 8 && row + direction >= 1 && row + direction <= 8) {
            ChessPiece rightPiece = board.getPiece(RCapture);
            if (rightPiece != null && !rightPiece.getTeamColor().equals(board.getPiece(position).getTeamColor())) {
                if (row + direction == 8 || row + direction == 1) {
                    moves.addAll(generatePromotionMoves(position, RCapture));
                }
                else {
                    moves.add(new ChessMove(position, RCapture, null));
                }
            }
        }
        return moves;
    }

    private Collection<? extends ChessMove> generatePromotionMoves(ChessPosition start, ChessPosition end) {
        Collection<ChessMove> promotionMoves = new ArrayList<>();

        promotionMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        promotionMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        promotionMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        promotionMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        return promotionMoves;
    }
}
