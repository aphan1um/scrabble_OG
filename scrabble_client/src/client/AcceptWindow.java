package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AcceptWindow {
    private JPanel Accept;
    private JButton acceptButton;
    private JButton refusedButton;
    public JPanel getPanel(){
        return this.Accept;
    }
    public JButton getAccept(){
        return this.acceptButton;
    }
    public JButton getRefused(){
        return this.refusedButton;
    }
}
