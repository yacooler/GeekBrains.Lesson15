package chat.server;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server {
    public Server() throws IOException{
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("Server is running on 8888");

            Socket client = serverSocket.accept();
            System.out.println(client);
            System.out.println(String.format("Client connected: %s", client.getLocalSocketAddress()));

            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            while (true) {
                String message = in.readUTF();
                if (message.equalsIgnoreCase("/end")) {
                    break;
                }
                out.writeUTF("Echo: " + message);
            }
        }
        finally{
            if (!Objects.isNull(in)) {in.close();}
            if (!Objects.isNull(out)) { out.close();}
        }
    }

    private void openGUI(){
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();
    }

}
