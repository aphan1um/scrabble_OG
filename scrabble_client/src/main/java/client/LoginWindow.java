package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow {
    private JPanel panel1;
    private JButton btnSignIn;
    private JTextField txtID;
    private JButton btnExit;
    private JTextField txtIP;
    private JTextField txtPort;
    private JButton btnHostGame;

    private static JFrame frame;

    private static final String WAIT_STR = "Please wait while we connect";

    public LoginWindow() {
        // press Sign In button
        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateTexts(false)) {
                    displayPleaseWait_Client();
                }
            }
        });

        // press Host button
        btnHostGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateTexts(true)) {
                    displayPleaseWait_Server();
                }
            }
        });

        frame = new JFrame("Welcome to Scrabble");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setSize(800, 400);

        // centre to screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    // TODO: Horrible code
    public void displayPleaseWait_Client() {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        JPanel panel = new JPanel();

        JLabel lblWait = new JLabel();

        panel.add(lblWait);

        lblWait.setText("<html>" +
                WAIT_STR + "..." + "<br/>" +
                "<center>" + txtIP.getText() + ":" + txtPort.getText() + "<center>" +
                "</html>");

        Thread t1 = new Thread(() -> {
            int i = 0;

            while (true) {
                i = (i + 1) % 4;
                lblWait.setText("<html>" +
                        WAIT_STR + "...".substring(0, i) + "<br/>" +
                        "<center>" + txtIP.getText() + ":" + txtPort.getText() + "<center>" +
                        "</html>");
                System.out.println(txtIP.getText());

                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    break;
                }
            }

            dialog.dispose();
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientMain.connectToServer(txtID.getText(),
                            txtIP.getText(), Integer.parseInt(txtPort.getText()));
                } catch (IOException e) {
                    e.printStackTrace();

                    t1.interrupt();
                    JOptionPane.showMessageDialog(panel1, "There was an error while connecting to the " +
                            "server. It is likely the server does not exist.", "Unable to connect",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // if we connect, we end up here
                t1.interrupt();
                frame.dispose();
                new LobbyWindow(frame);
            }
        });

        t1.start();
        t2.start();

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(panel1);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    // TODO: Horrible code
    public void displayPleaseWait_Server() {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        JPanel panel = new JPanel();

        JLabel lblWait = new JLabel();

        panel.add(lblWait);

        lblWait.setText("Creating server...");

        Thread t1 = new Thread(() -> {
            int i = 0;

            while (true) {
                i = (i + 1) % 4;
                lblWait.setText("Creating server" + "...".substring(0, i));

                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    break;
                }
            }

            dialog.dispose();
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientMain.createServer(txtID.getText(), Integer.parseInt(txtPort.getText()));
                } catch (IOException e) {
                    e.printStackTrace();

                    t1.interrupt();
                    JOptionPane.showMessageDialog(panel1, "There was an error while creating the server",
                            "Unable to create server",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // if we connect, we end up here
                t1.interrupt();
                frame.dispose();
                new LobbyWindow(frame);
            }
        });

        t1.start();
        t2.start();

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(panel1);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public boolean validateTexts(boolean isHosting) {
        if (txtPort.getText().isEmpty() ||
                (txtIP.getText().isEmpty() && !isHosting) ||
                txtPort.getText().isEmpty()) {
            JOptionPane.showMessageDialog(panel1,
                    "One of the entries is empty. Make sure they are filled with valid values.",
                    "Empty entry detected", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!txtPort.getText().matches("^[0-9]+$")) {
            JOptionPane.showMessageDialog(panel1,
                    "Entered port number is not a number.",
                    "Port number issue", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

}