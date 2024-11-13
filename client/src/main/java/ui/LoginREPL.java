package ui;

import client.ServerFacade;
import java.util.Scanner;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class LoginREPL {
    ServerFacade facade;

    public LoginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        boolean login = false;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to 240 Chess! Type Help to get started.");
        while(!login) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "help":
                    printHelp();
                    break;
                case "login":
                    if(input.length != 3) {
                        out.println("Please provide a username and a password before you press enter!");
                        out.println("login <USERNAME> <PASSWORD> - login to an existing user");
                        break;
                    }
                    if(facade.login(input[1], input[2])) {
                        out.println("You are now logged in!");
                        login = true;
                        break;
                    }
                case "quit":
                    return;

            }
        }
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printHelp() {
        out.println("Register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
        out.println("Login <USERNAME> <PASSWORD> - login to an existing user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }
}
