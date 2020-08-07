package chat.server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {

        try {
            Server server = new Server(9999);
            server.open();
            server.close();
        } catch (IOException | InterruptedException | RuntimeException e ) {
            e.printStackTrace();
        }

    }
}
