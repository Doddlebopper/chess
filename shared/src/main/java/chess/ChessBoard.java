package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
    }

    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;

        if(row < 0 || row >= 8 || col < 0 || col >= 8) {
            throw new IllegalArgumentException("Position out of bounds");
        }

        squares[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() -1;
        int col = position.getColumn() - 1;

        return squares[row][col];
    }

    public ChessBoard testBoard() {
        try {
            ChessBoard clonedBoard = (ChessBoard) super.clone();
            clonedBoard.squares = new ChessPiece[8][8];

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessPiece originalPiece = this.squares[i][j];
                    if (originalPiece != null) {
                        clonedBoard.squares[i][j] = originalPiece.clone();
                    }
                }
            }

            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();  // Should not happen, since we implement Cloneable
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        for(int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        squares[0][0] = squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        squares[7][0] = squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    public ChessPosition findKing(ChessGame.TeamColor team) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                ChessPiece piece = squares[i][j];
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == team) {
                    return new ChessPosition(i + 1, j + 1);
                }
            }
        }
        return null;
    }

    public List<ChessPosition> getAllPositions() {
        List<ChessPosition> positions = new ArrayList<>();

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                positions.add(new ChessPosition(i, j));
            }
        }
        return positions;
    }

    public ChessPiece movePiece(ChessPosition start, ChessPosition end) {
        ChessPiece pieceToMove = getPiece(start);
        if(pieceToMove == null) {
            throw new IllegalArgumentException("Non-existent piece");
        }

        ChessPiece capturedPiece = getPiece(end);

        addPiece(end, pieceToMove);
        squares[start.getRow() - 1][start.getColumn() - 1] = null;

        return capturedPiece;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                ChessPiece piece = squares[i][j];
                sb.append(piece == null ? "[ ]" : "[" + piece.getPieceType().name().charAt(0)+"]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
