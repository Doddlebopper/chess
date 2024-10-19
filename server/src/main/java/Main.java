import chess.*;
import server.Server;
import dataaccess.MemoryGameDAO; // Make sure to import your GameDAO implementation
import dataaccess.MemoryAuthDAO; // Make sure to import your AuthDAO implementation

public class Main {
    public static void main(String[] args) {
        // Instantiate your DAO implementations
        MemoryGameDAO gameDao = new MemoryGameDAO();
        MemoryAuthDAO authDao = new MemoryAuthDAO();

        // Create the Server instance with the DAOs
        Server myServer = new Server(gameDao, authDao);
        myServer.run(8080);

        System.out.println("♕ 240 Chess Server instantiated");
    }
}