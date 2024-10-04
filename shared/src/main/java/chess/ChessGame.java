package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame(ChessBoard board, TeamColor teamTurn) {
        this.teamTurn = teamTurn;
        this.board = board;
    }

    public ChessGame(){}

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null) {
            return null;
        }
        else {
            return piece.pieceMoves(board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);

        if(piece == null) {
            throw new InvalidMoveException("No Piece Exists");
        }

        if(piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong Team's Turn");
        }

        Collection<ChessMove> validMoves = validMoves(start);

        if(!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }

        ChessBoard simBoard = board.testBoard();
        simBoard.movePiece(start, end);

        if(isInCheck(teamTurn)) {
            throw new InvalidMoveException("Move puts the team in check");
        }

        board.movePiece(start, end);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.findKing(teamColor);

        for(ChessPosition oppPosition : board.getAllPositions()) {
            ChessPiece oppPiece = board.getPiece(oppPosition);
            if(oppPiece != null && oppPiece.getTeamColor() != teamColor); {
                if(oppPiece.pieceMoves(board, oppPosition).contains(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        for(ChessPosition position:board.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if(piece != null && piece.getTeamColor() != teamColor) {
                if(piece.pieceMoves(board, position).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
