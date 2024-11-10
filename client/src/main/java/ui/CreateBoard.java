package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class CreateBoard {
    ChessGame game;

    public CreateBoard(ChessGame game) {
        this.game = game;
    }

    public CreateBoard() {
        this.game = new ChessGame();
    }

    public void printBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
        StringBuilder output = new StringBuilder();
        output.append(SET_TEXT_BOLD);

        Collection<ChessMove> possibleMoves = selectedPos != null ? game.validMoves(selectedPos) : new HashSet<>();
        HashSet<ChessPosition> possibleSquares = new HashSet<>(possibleMoves != null ? possibleMoves.size() : 0);
        if (possibleMoves != null) {
            for (ChessMove move : possibleMoves) {
                possibleSquares.add(move.getEndPosition());
            }
        }
        boolean reversed = color == ChessGame.TeamColor.BLACK;
        int printCount = color == null ? 2 : 1;
        for (int j = 0; j < printCount; j++) {

            output.append(firstRow(reversed));

            for (int i = 8; i > 0; i--) {
                int row = !reversed ? i : (i * -1) + 9;
                output.append(otherRows(row, reversed, selectedPos, possibleSquares));
            }

            output.append(firstRow(reversed));
            if (j < printCount - 1) output.append("\n");

            reversed = !reversed;
        }
        output.append(RESET_TEXT_BOLD_FAINT);
        out.println(output);
        out.printf("Turn: %s\n", game.getTeamTurn().toString());
    }

    private String firstRow(boolean reversed) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(!reversed ? "    a  b  c  d  e  f  g  h    " : "    h  g  f  e  d  c  b  a    ");
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);
        output.append("\n");
        return output.toString();
    }

    private String otherRows(int row, boolean reversed, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));

        for (int i = 1; i < 9; i++) {
            int column = !reversed ? i : (i * -1) + 9;
            output.append(squareColor(row, column, startingSquare, highlightedSquares));
            output.append(piece(row, column));
        }


        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);

        output.append("\n");
        return output.toString();
    }

    private String squareColor(int row, int column, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        return (highlightedSquares.contains(new ChessPosition(row, column)) ? SET_BG_COLOR_YELLOW :
                (row + column) % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_DARK_GREY);
    }

    private String piece(int row, int column) {
        StringBuilder output = new StringBuilder();
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = game.getBoard().getPiece(position);

        if (piece != null) {

            output.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? SET_TEXT_COLOR_WHITE
                    : SET_TEXT_COLOR_BLACK);

            output.append(switch (piece.getPieceType()) {
                case QUEEN -> WHITE_QUEEN;
                case KING -> WHITE_KING;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            });
        } else {
            output.append(EMPTY);
        }

        return output.toString();
    }
}
