package chatrom_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatRom_Server {

    int SoLuong = 0;
    List<Socket> listSock = new ArrayList<>();
    List<String> users = new ArrayList<>();
    boolean flag = false;

    class LangNgheDuLieu extends Thread {

        Socket sk;
        String User;

        public LangNgheDuLieu(Socket socket) {
            this.sk = socket;
        }

        @Override
        public void run() {
            while (true) {
                DataInputStream br;
                try {
                    try {
                        br = new DataInputStream(sk.getInputStream());
                    } catch (SocketException ex) {
                        System.out.println("NGAT DOT NGOT");
                        users.remove(User);
                        for (int i = 0; i < listSock.size(); i++) {
                            if (listSock.get(i) == sk) {
                                listSock.get(i).close();
                                listSock.remove(i);
                                SoLuong--;
                            }
                        }
                        sk.close();
                        return;
                    }
                    String request = br.readUTF();
                    request = request.substring(0, request.indexOf("\r\n"));
                    System.out.println(request);
                    String[] data = request.split("!@#.");
                    if (data[1].equals("exit")) {
                        System.out.println("Dong socket");
                        users.remove(data[0]);
                        if (!data[0].equals("qt")) {
                            for (int i = 0; i < listSock.size(); i++) {
                                DataOutputStream ps = new DataOutputStream(listSock.get(i).getOutputStream());
                                ps.writeUTF("offline: " + data[0] + "\r\n");

                            }
                        }
                        for (int i = 0; i < listSock.size(); i++) {
                            if (listSock.get(i) == sk) {
                                listSock.get(i).close();
                                listSock.remove(i);
                                SoLuong--;
                            }
                        }
                        sk.close();
                        return;
                    }
                    if (data[0].equals("online")) {
                        if (!flag) {
                            User = data[1];
                            flag = true;
                        }
                        if (!users.contains(data[1])) {
                            users.add(data[1]);
                        }
                    }
                    if (data[1].equals("laydsonline")) {
                        String ds = "";
                        for (int i = 0; i < users.size(); i++) {
                            ds += users.get(i) + "!@#.";
                        }
                        DataOutputStream ps = new DataOutputStream(sk.getOutputStream());
                        ps.writeUTF("Danh sach:" + ds + "\r\n");
                        continue;
                    }
                    for (int i = 0; i < listSock.size(); i++) {
                        DataOutputStream ps = new DataOutputStream(listSock.get(i).getOutputStream());
                        if (listSock.get(i) != sk) {
                            ps.writeUTF((data[0] + ": " + data[1] + "\r\n"));
                        } else {
                            ps.writeUTF(("BẠN: " + data[1] + "\r\n"));
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("ngat dot ngot");
                    try {
                        users.remove(User);
                        for (int i = 0; i < listSock.size(); i++) {
                            if (listSock.get(i) == sk) {
                                listSock.get(i).close();
                                listSock.remove(i);
                                SoLuong--;
                            }
                        }
                        sk.close();
                        return;
                    } catch (IOException ex1) {
                        //Logger.getLogger(ChatRom_Server.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    //Logger.getLogger(ChatRom_Server.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    public void Server() {
        try {
            ServerSocket server = new ServerSocket(2015);
            while (true) {
                System.out.println("Dang cho ket noi....");
                Socket sk = server.accept();
                listSock.add(sk);
                new LangNgheDuLieu(sk).start();
                SoLuong++;
                System.out.println(SoLuong + " ket noi thanh cong");
            }

        } catch (IOException ex) {
            Logger.getLogger(ChatRom_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        // TODO code application logic here
        ChatRom_Server crs = new ChatRom_Server();
        crs.Server();
    }
}
