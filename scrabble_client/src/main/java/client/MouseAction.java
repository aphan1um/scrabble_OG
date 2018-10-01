package client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class MouseAction implements MouseListener {
    private LobbyWindow lobbyWindow;
    public void addReceivers(ArrayList invate, String receiver){
        if(!(invate.contains(receiver))){
            invate.add(receiver);
        }
        lobbyWindow.getWelcome().setText("");
        lobbyWindow.getWelcome().append("Invitation has been sent to "+receiver+" .");
    }
    public MouseAction(LobbyWindow lobbyWindow){
        this.lobbyWindow = lobbyWindow;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        addReceivers(lobbyWindow.getReceiver(), lobbyWindow.getUserlist().getSelectedValue().toString());
    }
    @Override
    public void mousePressed(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //Todo Auto-generated method
        lobbyWindow.getUserlist().setSelectionForeground(Color.red);
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseExited(MouseEvent e) {
        //Todo Auto-generated method
    }
}
