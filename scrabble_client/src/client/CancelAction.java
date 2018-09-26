package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelAction implements ActionListener {
    private JButton button;
    public CancelAction(JButton button){
        super();
        this.button = button;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
