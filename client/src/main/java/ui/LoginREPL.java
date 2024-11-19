package ui;

import client.ServerFacade;
import model.GameData;

import java.text.NumberFormat;
import java.util.*;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class LoginREPL {
    private final ServerFacade facade;
    List<GameData> games;
    private boolean login = false;

    public LoginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void preLoginRun() {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to 240 Chess! Type 'help' to get started.");
        String username = "";

        while(!login) {
            String[] input = getUserInputPre();
            switch (input[0].toLowerCase()) {
                case "help":
                    printHelpPre();
                    break;
                case "register":
                    if (input.length > 4) {
                        out.println("Please only provide a username, password, and email");
                        break;
                    }
                    else if(input.length < 4) {
                        out.println("Please provide a username, password, and email");
                        break;
                    }
                    if (input[2].length() < 4) {
                        out.println("Your password isn't strong enough. Try again!");
                        break;
                    }
                    if (facade.register(input[1], input[2], input[3])) {
                        out.println("You are now registered! Would you like to log in? (yes/no)");

                        Scanner scanner = new Scanner(System.in);
                        String response = scanner.nextLine().trim();
                        if(response.equalsIgnoreCase("yes")) {
                            login = true;
                            break;
                        }
                        else if (response.equalsIgnoreCase("no")) {
                            break;
                        }
                        else {
                            out.println("Invalid response. Returning to the main menu.");
                            break;
                        }
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
                        username = input[1];
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
                    break;
            }
        }

        postLoginRun(username);
    }

    public void postLoginRun(String username) {
        boolean inGame = false;
        List<GameData> games = new ArrayList<>();

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("You are now logged in! Type 'help' to get started!");
        while(login && !inGame) {
            String[] input = getUserInputPost();
            switch(input[0]) {
                case "help":
                    printHelpPost();
                    break;
                case "logout":
                    facade.logout();
                    login = false;
                    preLoginRun();
                    break;
                case "list":
                    games = new ArrayList<>();
                    HashSet<GameData> gameList = facade.listGames();
                    games.addAll(gameList);
                    for (int i = 0; i < games.size(); i++) {
                        GameData game = games.get(i);
                        String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                        String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
                        out.printf("Game %d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i + 1, game.gameName(), whiteUser, blackUser);
                    }
                    break;
                case "create":
                    if(input.length != 2) {
                        out.println("Please provide me with the game name!");
                    }
                    facade.createGame(input[1]);
                    HashSet<GameData> myGameList = facade.listGames();
                    int gameID = myGameList.size() - 1;
                    out.printf("Created game: %s%n", input[1]);
                    break;
                case "join":
                    if (input.length != 3) {
                        out.println("Please provide a game ID and color choice");
                        break;
                    }
                    if(!input[2].equalsIgnoreCase("white") && (!input[2].equalsIgnoreCase("black"))) {
                        out.println("Please provide either White or Black as your color!");
                        break;
                    }
                    try {
                        GameData joinGame = games.get(Integer.parseInt(input[1]) - 1);

                        if (facade.joinGame(joinGame.gameID(), input[2].toUpperCase())) {
                            out.println("You have joined the game");
                            new CreateBoard().printBoard(null);
                            break;
                        } else {
                            out.println("Game does not exist or color taken");
                            break;
                        }
                    }
                    catch(IndexOutOfBoundsException e) {
                        out.println("There are no games to join yet!");
                        break;
                    }
                    catch(NumberFormatException e) {
                        out.println("Not a valid integer");
                        break;
                    }
                case "observe":
                    if (input.length != 2) {
                        out.println("Please provide a game ID");
                        break;
                    }
                    if(Integer.parseInt(input[1]) < 0 || Integer.parseInt(input[1]) >= games.size()) {
                        out.println("Make sure the game a valid ID");
                        break;
                    }
                    GameData observeGame = games.get(Integer.parseInt(input[1]) - 1);
                    if (facade.joinGame(observeGame.gameID(), null)) {
                        out.println("You have joined the game as an observer");
                        new CreateBoard().printBoard(null);
                        break;
                    } else {
                        out.println("Game does not exist");
                        break;
                    }
                case "quit":
                    return;
                default:
                    out.println("Command not recognized! Here are your options:\n");
                    printHelpPost();
            }
        }
    }

    private void refreshGameList() {
        games = new ArrayList<>();
        HashSet<GameData> gameList = facade.listGames();
        games.addAll(gameList);
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
