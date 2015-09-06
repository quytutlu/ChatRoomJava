package chatroom;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatRoom_Client {

    public void connect() {
        try {
            //Socket socket = new Socket("localhost", 2015);
            Socket socket = new Socket("52.68.172.187", 2015);
            //new FormChatRoom(socket);
            new login(socket);
        } catch (IOException ex) {
            Logger.getLogger(ChatRoom_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        ChatRoom_Client crc=new ChatRoom_Client();
        crc.connect();
    }
}
