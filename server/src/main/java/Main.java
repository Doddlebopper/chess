import dataaccess.*;
import server.Server;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e ) {
            throw new RuntimeException(e);
        }

        Server myServer = new Server();
        myServer.run(8080);

        System.out.println("♕ 240 Chess Server instantiated");
    }
}