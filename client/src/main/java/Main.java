import chess.*;
import ui.CreateBoard;

public class Main {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        CreateBoard board = new CreateBoard(game);
        board.printBoard(ChessGame.TeamColor.WHITE, null);
    }
}