package client;

public class ServerFacade {
    HTTPCommunicator http;
    String domain;
    String authToken;

    public ServerFacade() throws Exception {
        this("localhost:8080");
    }

    public ServerFacade(String domain) {
        this.domain = domain;
        http = new HTTPCommunicator(this, domain);
    }




    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
