package chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner console;
    private boolean isChatAlive = false;
    private Thread sendMessagesThread;
    private Thread receiveMessagesThread;



    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        System.out.println(String.format("Client connect to %s:%d", host, port));
    }

    public void open() throws IOException, InterruptedException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        console = new Scanner(System.in);
        setChatAlive(true);
        receiveMessagesThread = createReceiveMessagesThread();
        sendMessagesThread = createSendMessagesThread();

        receiveMessagesThread.start();
        sendMessagesThread.start();

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

                    //Если от сервера пришел exit отправим ему confirm чтобы закрыть ждущий in
                    if (message.equalsIgnoreCase("exit")){
                        System.out.println("server typed exit");
                        System.out.println("send confirm to the server");
                        out.writeUTF("exit confirmed by the client");
                        System.out.println("Press Enter to exit (не знаю, как прекратить ожидающий ввод с консоли другими средствами)");
                    }

                    setChatAlive(false);

                } catch (IOException e) {
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

                    if (message.equalsIgnoreCase("exit")){
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
        console.close();
    }

}


