package pkgfinal;

import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class CaroServer {

        public static JFrame f;
        JButton[][] btn;
        boolean winner;
        JButton send;
        String temp = "";
        JTextArea content;
        JTextField nhap, enterchat;
        JPanel p;
        int xx, yy, x, y;
        int[][] maTran;
        int[][] maTranDanhDau;

        // Server Socket
        ServerSocket serversocket;
        Socket socket;
        OutputStream os;
        InputStream is;
        ObjectOutputStream oos;
        ObjectInputStream ois;

        // MenuBar
        MenuBar menubar;

        public CaroServer() {
                f = new JFrame();
                f.setTitle("Game Caro");
                f.setSize(750, 500);
                x = 25;
                y = 25;
                f.getContentPane().setLayout(null);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                f.setResizable(false);

                maTran = new int[x][y];
                maTranDanhDau = new int[x][y];
                menubar = new MenuBar();
                p = new JPanel();
                p.setBounds(10, 30, 400, 400);
                p.setLayout(new GridLayout(x, y));
                f.add(p);

                f.setMenuBar(menubar);
                Menu game = new Menu("Game");
                menubar.add(game);
                Menu help = new Menu("Help");
                menubar.add(help);
                MenuItem helpItem = new MenuItem("Help");
                help.add(helpItem);
                help.addSeparator();
                MenuItem about = new MenuItem("About");
                help.add(about);
                MenuItem newItem = new MenuItem("New Game");
                game.add(newItem);
                game.addSeparator();
                MenuItem exit = new MenuItem("Exit");
                game.add(exit);
                newItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                newgame();
                                try {
                                        oos.writeObject("newgame,123");
                                } catch (IOException e2) {
                                        e2.printStackTrace();
                                }
                        }

                });
                exit.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                        }
                });
                about.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                // Object[] options = {"OK"};
                                JOptionPane.showConfirmDialog(f,
                                                "Game caro", "Information",
                                                JOptionPane.CLOSED_OPTION);
                        }
                });
                help.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                // Object[] options = {"OK"};
                                JOptionPane.showConfirmDialog(f,
                                                "Players take turns placing their pieces on the grid\n"
                                                                + "The objective is to be the first to create a line of five of \n"
                                                                + "their pieces horizontally, vertically, or diagonally",
                                                "Rules",
                                                JOptionPane.CLOSED_OPTION);
                        }
                });
                // khung chat
                Font fo = new Font("Arial", Font.BOLD, 15);
                content = new JTextArea();
                content.setFont(fo);
                content.setBackground(Color.white);

                content.setEditable(false);
                JScrollPane sp = new JScrollPane(content);
                sp.setBounds(423, 30, 300, 340);
                send = new JButton("Send");
                send.setBounds(640, 390, 70, 40);
                enterchat = new JTextField("");
                enterchat.setFont(fo);
                enterchat.setBounds(423, 400, 200, 30);
                enterchat.setBackground(Color.white);
                f.add(enterchat);
                f.add(send);
                f.add(sp);
                f.setVisible(true);
                send.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try {

                                        temp += "You: " + enterchat.getText() + "\n";
                                        content.setText(temp);
                                        oos.writeObject("chat," + enterchat.getText());
                                        enterchat.setText("");
                                        enterchat.requestFocus();
                                        content.setVisible(false);
                                        content.setVisible(true);

                                } catch (Exception e2) {
                                        e2.printStackTrace();
                                }
                        }
                });

                // button caro
                btn = new JButton[x][y];
                for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                                final int a = i, b = j;
                                btn[a][b] = new JButton();
                                btn[a][b].setBackground(Color.white);
                                btn[a][b].addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                                maTranDanhDau[a][b] = 1;
                                                btn[a][b].setEnabled(false);
                                                // btn[a][b].setIcon(new ImageIcon(getClass().getResource("o.png")));
                                                btn[a][b].setBackground(Color.RED);
                                                ;
                                                try {
                                                        oos.writeObject("caro," + a + "," + b);
                                                        setEnableButton(false);
                                                } catch (Exception e2) {
                                                        e2.printStackTrace();
                                                }
                                        }

                                });
                                p.add(btn[a][b]);
                                p.setVisible(false);
                                p.setVisible(true);
                        }
                }

                try {
                        serversocket = new ServerSocket(1234);
                        System.out.println("Waiting for client...");
                        socket = serversocket.accept();
                        System.out.println("Client connected!");
                        os = socket.getOutputStream();
                        is = socket.getInputStream();
                        oos = new ObjectOutputStream(os);
                        ois = new ObjectInputStream(is);
                        while (true) {
                                String stream = ois.readObject().toString();
                                String[] data = stream.split(",");
                                if (data[0].equals("chat")) {
                                        temp += "Guest:" + data[1] + '\n';
                                        content.setText(temp);
                                } else if (data[0].equals("caro")) {
                                        caro(data[1], data[2]);
                                        setEnableButton(true);

                                        if (winner == false)
                                                setEnableButton(true);
                                } else if (data[0].equals("newgame")) {
                                        newgame();
                                } else if (data[0].equals("confirmNewGame")) {
                                        int m = JOptionPane.showConfirmDialog(f,
                                                        "The opponent has challenged you to play again. Would you like to play?",
                                                        "Notifications",
                                                        JOptionPane.YES_NO_OPTION);
                                        if (m == JOptionPane.YES_OPTION) {
                                                newgame();
                                                try {
                                                        oos.writeObject("newgame,123");
                                                } catch (IOException e2) {
                                                        e2.printStackTrace();
                                                }
                                        }
                                }
                        }
                } catch (Exception e2) {
                        e2.printStackTrace();
                }

        }

        public void newgame() {
                for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                                btn[i][j].setBackground(Color.white);
                                maTran[i][j] = 0;
                                maTranDanhDau[i][j] = 0;
                        }
                }
                setEnableButton(true);
        }

        public void setEnableButton(boolean b) {
                for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                                if (maTranDanhDau[i][j] == 0)
                                        btn[i][j].setEnabled(b);
                        }
                }
        }

        // thuat toan tinh thang thua
        public int checkHang() {
                int win = 0, hang = 0;
                boolean check = false;
                for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                                if (check) {
                                        if (maTran[i][j] == 1) {
                                                hang++;
                                                if (hang > 4) {
                                                        win = 1;
                                                        break;
                                                }
                                                continue;
                                        } else {
                                                check = false;
                                                hang = 0;
                                        }
                                }
                                if (maTran[i][j] == 1) {
                                        check = true;
                                        hang++;
                                } else {
                                        check = false;
                                }
                        }
                        hang = 0;
                }
                return win;
        }

        public int checkCot() {
                int win = 0, cot = 0;
                boolean check = false;
                for (int j = 0; j < y; j++) {
                        for (int i = 0; i < x; i++) {
                                if (check) {
                                        if (maTran[i][j] == 1) {
                                                cot++;
                                                if (cot > 4) {
                                                        win = 1;
                                                        break;
                                                }
                                                continue;
                                        } else {
                                                check = false;
                                                cot = 0;
                                        }
                                }
                                if (maTran[i][j] == 1) {
                                        check = true;
                                        cot++;
                                } else {
                                        check = false;
                                }
                        }
                        cot = 0;
                }
                return win;
        }

        public int checkCheoPhai() {
                int win = 0, cheop = 0, n = 0;
                boolean check = false;
                for (int i = x - 1; i >= 0; i--) {
                        for (int j = 0; j < y; j++) {
                                if (check) {
                                        if (maTran[n - j][j] == 1) {
                                                cheop++;
                                                if (cheop > 4) {
                                                        win = 1;
                                                        break;
                                                }
                                                continue;
                                        } else {
                                                check = false;
                                                cheop = 0;
                                        }
                                }
                                if (maTran[i][j] == 1) {
                                        n = i + j;
                                        check = true;
                                        cheop++;
                                } else {
                                        check = false;
                                }
                        }
                        cheop = 0;
                        check = false;
                }
                return win;
        }

        public int checkCheoTrai() {
                int win = 0, cheot = 0, n = 0;
                boolean check = false;
                for (int i = 0; i < x; i++) {
                        for (int j = y - 1; j >= 0; j--) {
                                if (check) {
                                        if (maTran[n - j - 2 * cheot][j] == 1) {
                                                cheot++;
                                                System.out.print("+" + j);
                                                if (cheot > 4) {
                                                        win = 1;
                                                        break;
                                                }
                                                continue;
                                        } else {
                                                check = false;
                                                cheot = 0;
                                        }
                                }
                                if (maTran[i][j] == 1) {
                                        n = i + j;
                                        check = true;
                                        cheot++;
                                } else {
                                        check = false;
                                }
                        }
                        n = 0;
                        cheot = 0;
                        check = false;
                }
                return win;
        }

        public void caro(String x, String y) {
                xx = Integer.parseInt(x);
                yy = Integer.parseInt(y);
                // danh dau vi tri danh
                maTran[xx][yy] = 1;
                maTranDanhDau[xx][yy] = 1;
                btn[xx][yy].setEnabled(false);
                // btn[xx][yy].setIcon(new ImageIcon("x.png"));
                btn[xx][yy].setBackground(Color.BLUE);

                // Kiem tra thang hay chua
                winner = (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1 || checkCheoTrai() == 1);
                if (winner) {
                        setEnableButton(false);
                        int m = JOptionPane.showConfirmDialog(f,
                                        "You have lost. Would you like to challenge the opponent again?",
                                        "Notifications",
                                        JOptionPane.YES_NO_OPTION);
                        if (m == JOptionPane.YES_OPTION) {
                                try {
                                        oos.writeObject("confirmNewGame,123");
                                } catch (IOException e2) {
                                        e2.printStackTrace();
                                }
                        } else if (m == JOptionPane.NO_OPTION) {
                        }
                }

        }

        public static void main(String[] args) {
                new CaroServer();
        }

}
