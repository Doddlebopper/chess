package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class CreateBoard {
    ChessGame game;

    public CreateBoard(ChessGame game) {
        this.game = game;
    }

    public CreateBoard() {
        this.game = new ChessGame();
    }

    public void printBoard(ChessPosition selectedPos) {

        String output = SET_TEXT_BOLD +
                "\nWhite\n" +
                generateBoard(ChessGame.TeamColor.WHITE, selectedPos) +
                "\n" +
                "\nBlack\n" +
                generateBoard(ChessGame.TeamColor.BLACK, selectedPos) +
                RESET_TEXT_BOLD_FAINT;
        out.println(output);
    }

    public String generateBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
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
                output.append(otherRows(row, reversed, possibleSquares));
            }

            output.append(firstRow(reversed));
            if (j < printCount - 1) output.append("\n");

            reversed = !reversed;
        }
        output.append(RESET_TEXT_BOLD_FAINT);
        return output.toString();
    }

    private String firstRow(boolean reversed) {
        String rowLabel = reversed ? "    h  g  f  e  d  c  b  a    " : "    a  b  c  d  e  f  g  h    ";
        return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_GREEN + rowLabel + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
    }

    private String otherRows(int row, boolean reversed, HashSet<ChessPosition> highlightedSquares) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_WHITE);
        output.append(SET_TEXT_COLOR_GREEN);
        output.append(" %d ".formatted(row));

        for (int i = 1; i < 9; i++) {
            int column = !reversed ? i : (i * -1) + 9;
            output.append(squareColor(row, column, highlightedSquares));
            output.append(piece(row, column));
        }

        output.append(SET_BG_COLOR_WHITE);
        output.append(SET_TEXT_COLOR_GREEN);
        output.append(" %d ".formatted(row));
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);

        output.append("\n");
        return output.toString();
    }

    private String squareColor(int row, int column, HashSet<ChessPosition> highlightedSquares) {
        ChessPosition square = new ChessPosition(row, column);
        if (highlightedSquares.contains(square)) {
            return SET_BG_COLOR_DARK_GREEN;
        } else if ((row + column) % 2 == 0) {
            return SET_BG_COLOR_BLUE;
        } else {
            return SET_BG_COLOR_MAGENTA;
        }
    }

    private String piece(int row, int column) {
        StringBuilder output = new StringBuilder();
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = game.getBoard().getPiece(position);

        if (piece != null) {
            output.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
            output.append(" ").append(switch (piece.getPieceType()) {
                case QUEEN -> "Q";
                case KING -> "K";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case ROOK -> "R";
                case PAWN -> "P";
            }).append(" ");
        } else {
            output.append("   ");
        }

        return output.toString();
    }
}
