import chess.*;
import server.Server;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;

public class Main {
    public static void main(String[] args) {
        MemoryGameDAO gameDao = new MemoryGameDAO();
        MemoryAuthDAO authDao = new MemoryAuthDAO();
        MemoryUserDAO userDao = new MemoryUserDAO();

        Server myServer = new Server(gameDao, authDao, userDao);
        myServer.run(8080);

        System.out.println("♕ 240 Chess Server instantiated");
    }
}