package chat.client;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args) {

        try {
            Client client = new Client("localhost", 9999);
            client.open();
            client.close();
        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }

    }
}
