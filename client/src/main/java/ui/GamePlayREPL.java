package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

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

    public void run() {

    }
}
