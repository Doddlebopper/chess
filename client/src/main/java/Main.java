import chess.*;
import client.ServerFacade;
import ui.LoginREPL;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client:");

        ServerFacade facade = new ServerFacade();

        LoginREPL login = new LoginREPL(facade);
        login.run();
        System.out.println("Done!");
    }
}