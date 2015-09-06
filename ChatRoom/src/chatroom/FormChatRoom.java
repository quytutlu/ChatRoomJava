package chatroom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import net.sf.jcarrierpigeon.WindowPosition;
import net.sf.jtelegraph.Telegraph;
import net.sf.jtelegraph.TelegraphQueue;
import net.sf.jtelegraph.TelegraphType;

/**
 *
 * @author QuyTuTlu
 */
public class FormChatRoom extends JFrame {

    //private final JButton logout;
    private JButton Gui;
    private JTextArea ta;
    private JTextArea tf;
    private String TenDangNhap;

    public FormChatRoom(Socket socket, login lg, String TenDangNhap) {
        DataOutputStream pst;
        try {
            pst = new DataOutputStream(socket.getOutputStream());
            pst.write(("online!@#." + TenDangNhap + "\r\n").getBytes("UTF8"));
        } catch (IOException ex) {
            Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.TenDangNhap = TenDangNhap;
        lg.setVisible(false);
        JPanel KhungChat = new JPanel(new GridLayout(1, 1));
        class HienThiThongBao extends Thread {

            String mess;
            String Tile;
            TelegraphType type;
            int time;
            WindowPosition windowPosition;

            public HienThiThongBao(String NoiDung) {
                this.mess = NoiDung;
                this.Tile = "Bạn có một tin nhắn mới";
                this.type = TelegraphType.MAIL;
                time = 3000;
                this.windowPosition = WindowPosition.BOTTOMRIGHT;
            }

            public HienThiThongBao(String tile, String NoiDung, TelegraphType type, WindowPosition windowPosition, int time) {
                this.Tile = tile;
                this.mess = NoiDung;
                this.type = type;
                this.time = time;
                this.windowPosition = windowPosition;
            }

            @Override
            public void run() {
                Telegraph tele = new Telegraph(Tile, mess, type, windowPosition, time);
                TelegraphQueue q = new TelegraphQueue();
                q.add(tele);
            }

        }
        class LangNgheDuLieu extends Thread {

            Socket socket;

            public LangNgheDuLieu(Socket socket) {
                this.socket = socket;

            }

            @Override
            public void run() {
                while (true) {
                    //BufferedReader br;
                    DataInputStream br;
                    try {
                        //br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                        br = new DataInputStream(socket.getInputStream());
                        //String str = br.readLine();
                        byte[] data = new byte[1024];
                        int count = br.read(data);
                        String str = null;
                        str = new String(data, "UTF-8");
                        str = str.substring(0, str.indexOf("\r\n"));
                        System.out.println(str);
                        String[] stm = str.split(":");
                        if (stm[0].equals("Danh sach")) {
                            String[] UserOnline = stm[1].split("!@#.");
                            int sl = UserOnline.length;
                            String onlineTile = "Số lượng online(" + sl + ")";
                            String ListOnline = "";
                            for (int i = 0; i < sl; i++) {
                                ListOnline += (i + 1) + ": " + UserOnline[i] + "<br>";
                            }
                            //ta.setText(ta.getText() + online + "\n");
                            new HienThiThongBao(onlineTile, ListOnline, TelegraphType.STAR_FULL, WindowPosition.TOPRIGHT, 10000).start();
                            continue;
                        }
                        if (!stm[0].equals("BẠN")) {
                            if (stm[0].equals("online")) {
                                new HienThiThongBao("ONLINE", stm[1] + " vừa online", TelegraphType.USER, WindowPosition.BOTTOMRIGHT, 5000).start();
                                //new HienThiThongBao("ONLINE")
                            } else if (stm[0].equals("offline")) {
                                new HienThiThongBao("OFFLINE", stm[1] + " vừa offline", TelegraphType.USER_DELETE, WindowPosition.BOTTOMRIGHT, 5000).start();
                            } else {
                                new HienThiThongBao(str).start();
                            }
                        }
                        try {
                            ta.setText(ta.getText() + str + "\n");
                        } catch (java.lang.NullPointerException ex) {
                            //Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        LangNgheDuLieu ln = new LangNgheDuLieu(socket);
        ln.start();
        tf = new JTextArea("", 2, 1);
        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String data = tf.getText().trim();
                    if (data.equals("online")) {
                        DataOutputStream ps;
                        try {
                            ps = new DataOutputStream(socket.getOutputStream());
                            ps.write((TenDangNhap + "!@#.laydsonline\r\n").getBytes("UTF8"));
                            tf.setText("");
                            e.consume();
                        } catch (IOException ex) {
                            Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return;
                    }
                    if (!data.equals("")) {
                        DataOutputStream ps;
                        try {
                            ps = new DataOutputStream(socket.getOutputStream());
                            ps.write((TenDangNhap + "!@#." + tf.getText() + "\r\n").getBytes("UTF8"));
                            tf.setText("");
                            e.consume();
                        } catch (IOException ex) {
                            Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        e.consume();
                        //JOptionPane.showMessageDialog(null, "trong");
                    }
                }
            }

        });
        //tf.setUI(new te);
        tf.setBackground(Color.WHITE);
        tf.setWrapStyleWord(true);
        tf.setLineWrap(true);
        tf.setFont(new java.awt.Font("Arial", 0, 15));
        JPanel NutGui = new JPanel(new GridLayout(1, 1));
        Gui = new JButton("Send");
        Gui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data = tf.getText().trim();
                if (data.equals("online")) {
                    DataOutputStream ps;
                    try {
                        ps = new DataOutputStream(socket.getOutputStream());
                        ps.write((TenDangNhap + "!@#.laydsonline\r\n").getBytes("UTF8"));
                        tf.setText("");
                    } catch (IOException ex) {
                        Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                if (!data.equals("")) {
                    DataOutputStream ps;
                    try {
                        ps = new DataOutputStream(socket.getOutputStream());
                        ps.write((TenDangNhap + "!@#." + tf.getText() + "\r\n").getBytes("UTF8"));
                        tf.setText("");
                    } catch (IOException ex) {
                        Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        Gui.setVisible(false);
        KhungChat.add(new JScrollPane(tf));
        //NutGui.add(Gui);
        //KhungChat.add(NutGui);

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome " + TenDangNhap + " to the Chat room\n", 80, 80);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setFont(new java.awt.Font("Arial", 0, 15));
        ta.setText(ta.getText() + "Tác giả: Nguyễn Quý Tú\n");
        ta.setText(ta.getText() + "---------------------------------------\n");
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        DefaultCaret caret = (DefaultCaret) ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(ta);
        scrollPane.setViewportView(ta);
        centerPanel.add(scrollPane);
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);
        add(KhungChat, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PrintStream ps;
                try {
                    ps = new PrintStream(socket.getOutputStream());
                    ps.println(TenDangNhap + "!@#.exit\r\n");
                    socket.close();
                    ln.stop();
                } catch (IOException ex) {
                    Logger.getLogger(FormChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        this.setTitle("Chat room");
        setVisible(true);
        tf.requestFocus();
    }
}
