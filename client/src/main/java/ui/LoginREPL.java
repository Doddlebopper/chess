package ui;

import client.ServerFacade;
import model.GameData;

import java.util.*;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class LoginREPL {
    private final ServerFacade facade;
    private boolean login = false;

    public LoginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void preLoginRun() {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to 240 Chess! Type 'help' to get started.");

        while(!login) {
            String[] input = getUserInputPre();
            switch (input[0].toLowerCase()) {
                case "help":
                    printHelpPre();
                    break;
                case "register":
                    if (input.length > 4) {
                        out.println("Please only provide a username, password, and email");
                    }
                    else if(input.length < 4) {
                        out.println("Please provide a username, password, and email");
                    }
                    if (facade.register(input[1], input[2], input[3])) {
                        out.println("You are now registered and logged in");
                        login = true;
                        break;
                    } else {
                        out.println("Username already in use, please choose a new one");
                        break;
                    }
                case "login":
                    if(input.length != 3) {
                        out.println("Please provide a username and a password before you press enter!");
                        break;
                    }
                    if(facade.login(input[1], input[2])) {
                        out.println("You are now logged in!");
                        login = true;
                        break;
                    }
                    else {
                        out.println("Your username or password is incorrect!");
                        break;
                    }
                case "quit":
                    return;
                default:
                    out.println("Command not recognized, please try again!");
                    printHelpPre();
            }
        }

        postLoginRun();
    }

    public void postLoginRun() {
        boolean inGame = false;
        List<GameData> games = new ArrayList<>();

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while(login && !inGame) {
            String[] input = getUserInputPost();
            switch(input[0]) {
                case "help":
                    printHelpPost();
                case "logout":
                    facade.logout();
                    login = false;
                    preLoginRun();
                case "list":
                    HashSet<GameData> gameList = facade.listGames();
                    games.addAll(gameList);
                    for (int i = 0; i < games.size(); i++) {
                        GameData game = games.get(i);
                        String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                        String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
                        out.printf("%d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
                    }
                    break;
                case "create":
                    if(input.length != 2) {
                        out.println("Please provide me with your name!");
                    }
                    facade.createGame(input[1]);
                    out.printf("Created game: %s%n", input[1]);
                    break;
                case "join":
                    facade.joinGame();
                case "observe":
                    facade.observeGame();
                case "quit":
                    return;
                default:
                    out.println("Command not recognized! Here are your options:\n");
                    printHelpPost();
            }
        }
    }

    private String[] getUserInputPre() {
        out.print("\n[LOGGED OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private String[] getUserInputPost() {
        out.print("\n[LOGGED IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printHelpPre() {
        out.println("Register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
        out.println("Login <USERNAME> <PASSWORD> - login to an existing user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printHelpPost() {
        out.println("create <NAME> - create a new game");
        out.println("list - list all games");
        out.println("join <ID> [WHITE|BLACK] - join a game as color");
        out.println("observe <ID> - observe a game");
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }
}
