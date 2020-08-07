package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner console;
    private boolean isChatAlive = false;
    private Thread sendMessagesThread;
    private Thread receiveMessagesThread;



    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println(String.format("Server is running on %d", port));
    }

    public void open() throws IOException, InterruptedException {
        System.out.println("Wait for the Client");
        socket = serverSocket.accept();
        System.out.println(String.format("Client connected: %s", socket.getLocalSocketAddress()));
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        console = new Scanner(System.in);
        setChatAlive(true);
        receiveMessagesThread = createReceiveMessagesThread();
        sendMessagesThread = createSendMessagesThread();

        receiveMessagesThread.start();
        sendMessagesThread.start();


        //цепляемся за поток отправки, т.к. последнее слово всегда за ним - он отправляет противоположной стороне подтверждение выхода
        receiveMessagesThread.join();

    }

    private Thread createReceiveMessagesThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "";

                try {
                    do {
                        if (isChatAlive()) {
                            message = in.readUTF();
                        }
                        System.out.println(message);
                    } while ((!message.equalsIgnoreCase("exit")) && isChatAlive());

                    //Если от клиента пришел exit, отправим ему подтверждение, чтобы закрыть ждущий in-поток
                    if (message.equalsIgnoreCase("exit")){
                        System.out.println("client typed exit");
                        System.out.println("send confirm to the client");
                        out.writeUTF("exit confirmed by the server");
                        System.out.println("Press Enter to exit (не знаю, как прекратить ожидающий ввод с консоли другими средствами)");
                    }

                    setChatAlive(false);

                } catch (IOException e) {
                    //Пробрасываем рантайм выше, т.к. Runnable не позволяет пробросить IOException
                    setChatAlive(false);
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private Thread createSendMessagesThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                try {
                    do {
                        message = console.nextLine();
                        if (isChatAlive()) {
                            out.writeUTF(message);
                        }
                    } while ((!message.equalsIgnoreCase("exit")) && isChatAlive());

                    if (message.equalsIgnoreCase("exit")) {
                        System.out.println("do exit");
                    }

                    setChatAlive(false);

                } catch (IOException e) {
                    //Пробрасываем рантайм выше, т.к. Runnable не позволяет пробросить IOException
                    setChatAlive(false);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean isChatAlive() {
        return isChatAlive;
    }

    public void setChatAlive(boolean chatAlive) {
        isChatAlive = chatAlive;
    }

    public void close() throws IOException{
        socket.close();
        in.close();
        out.close();
        serverSocket.close();
        console.close();
    }

}
