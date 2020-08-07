package chat.client;

import chat.gui.ChatFrame;
import chat.gui.MessageListener;

public class Client {

    ChatFrame chatFrame = new ChatFrame(
            "Клиентское приложение",
            new MessageListener() {
                @Override
                public void messagePerformed(String message) {
                    sendMessage(message);
                }
            });


    private void sendMessage(String message){
        System.out.println(message);
    }

}
