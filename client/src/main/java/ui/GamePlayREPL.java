package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GamePlayREPL {

    public static CreateBoard CreateBoard;
    ServerFacade facade;
    public static CreateBoard board;
    ChessGame game;
    int gameID;
    public static ChessGame.TeamColor color;

    public GamePlayREPL(ServerFacade facade, GameData data, ChessGame.TeamColor color) {
        this.facade = facade;
        this.game = data.game();
        this.gameID = data.gameID();
        GamePlayREPL.color = color;

        board = new CreateBoard(game);
    }

    public void run() throws IllegalAccessException {
        boolean inGame = true;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while(inGame) {
            String[] input = getUserInput();
            switch(input[0]) {
                case "help":
                    out.println("redraw - redraw the game board");
                    out.println("leave - leave the current game");
                    out.println("move <from> <to> <promotion_piece> - make a move");
                    out.println("resign - forfeit this game");
                    out.println("highlight <coordinate> - highlight all legal moves for the given piece");
                    out.println("help - show this menu");
                    break;
                case "redraw":
                    CreateBoard.generateBoard(color, null);
                    break;
                case "leave":
                    inGame = false;
                    facade.userLeave(gameID);
                    break;
                case "move":
                    if(input.length >= 3 && input[1].matches("[a-h][1-8]") && input[2].matches("[a-h][1-8]")) {
                        ChessPosition oldPosition = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a'- 1));
                        ChessPosition newPosition = new ChessPosition(input[2].charAt(1) - '0', input[2].charAt(0) - ('a'- 1));

                        ChessPiece.PieceType promotionPiece = null;
                        if(input.length == 4) {
                            promotionPiece = pieceType(input[3]);
                            if(promotionPiece == null) {
                                out.println("Please provide proper promotion piece name");
                            }
                        }

                        facade.makeMove(gameID, new ChessMove(oldPosition, newPosition, promotionPiece));
                    }
                    else {
                        out.println("Coordinates should be for example 'c3 d5'");
                        break;
                    }
                case "resign":
                    out.println("Are you sure you want to resign? (yes/no)");
                    String prompt = "IN-GAME";
                    out.printf("\n[%s] >>> ", prompt);
                    Scanner scanner = new Scanner(System.in);
                    String[] userAnswer = scanner.nextLine().split(" ");
                    if(userAnswer.length == 1 && userAnswer[0].equalsIgnoreCase("yes")) {
                        facade.userResign(gameID);
                    }
                    else {
                        out.println("resign cancelled");
                    }
                    break;
                case "highlight":
                    if(input.length == 2 && input[1].matches("[a-h][1-8]")) {
                        ChessPosition position = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
                        board.generateBoard(color, position);
                    }
                    else {
                        out.println("Please provide a coordinate like 'a1'");
                    }
                    break;
                default:
                    out.println("Command not recognized");
                    break;
            }
        }
    }

    private ChessPiece.PieceType pieceType(String name) {
        return switch(name.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "PAWN" -> ChessPiece.PieceType.PAWN;
            default -> null;
        };
    }

    private String[] getUserInput() {
        String input = "IN-GAME";
        out.printf("\n[%s] >>> ", input);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }
}
