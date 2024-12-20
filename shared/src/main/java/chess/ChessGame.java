package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean gameOver;

    public ChessGame(ChessBoard board, TeamColor teamTurn) {
        this.teamTurn = teamTurn;
        this.board = board;
    }

    public ChessGame(){
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

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


        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();

        for(ChessMove move : allMoves) {
            ChessBoard testBoard = board.testBoard();

            testBoard.movePiece(startPosition, move.getEndPosition());

            ChessGame tempGame = new ChessGame(testBoard, this.getTeamTurn());
            if(!tempGame.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
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

        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if((piece.getTeamColor() == TeamColor.WHITE && end.getRow() == 8) || (piece.getTeamColor() == TeamColor.BLACK && end.getRow() == 1)) {

                ChessPiece.PieceType promotionType = move.getPromotionPiece();

                ChessPiece promotePiece = new ChessPiece(piece.getTeamColor(), promotionType);

                board.addPiece(end, promotePiece);

                board.addPiece(start, null);
            }
        }

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
        ChessBoard testBoard = board.testBoard();

        for(ChessPosition oppPosition : testBoard.getAllPositions()) {
            ChessPiece oppPiece = testBoard.getPiece(oppPosition);

            if(oppPiece != null && oppPiece.getTeamColor() != teamColor) {
                Collection<ChessMove> validMoves = oppPiece.pieceMoves(testBoard, oppPosition);

                for(ChessMove move : validMoves) {
                    if(move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (ChessPosition position : board.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);

            if (piece != null && piece.getTeamColor() == teamColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, position);

                for (ChessMove move : moves) {
                    ChessBoard tempBoard = board.testBoard();
                    tempBoard.movePiece(move.getStartPosition(), move.getEndPosition());

                    ChessGame tempGame = new ChessGame();
                    tempGame.setBoard(tempBoard);
                    tempGame.setTeamTurn(teamColor);

                    if (!tempGame.isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
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
            if(piece != null && piece.getTeamColor() == teamColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
                for(ChessMove move : validMoves) {
                    ChessBoard testBoard = board.testBoard();
                    testBoard.movePiece(position, move.getEndPosition());

                    ChessGame tempGame = new ChessGame();
                    tempGame.setBoard(testBoard);
                    tempGame.setTeamTurn(teamColor);

                    if(!tempGame.isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
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

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean getGameOver() {
        return gameOver;
    }
}
